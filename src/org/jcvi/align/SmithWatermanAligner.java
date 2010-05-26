/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 *  This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/**
 * SmithWatermanAligner.java Created: Aug 10, 2009 - 11:16:37 AM (jsitz) Copyright 2009 J.
 * Craig Venter Institute
 */
package org.jcvi.align;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;


/**
 * A <code>SmithWatermanAligner</code> is a simple implementation of an {@link Aligner} using an
 * unoptimized <a href="http://en.wikipedia.org/wiki/Smith-Waterman_algorithm">Smith Waterman</a>
 * algorithm.
 * 
 * @author jsitz@jcvi.org
 */
public class SmithWatermanAligner implements Aligner<NucleotideGlyph>
{
    /** The default gap score. */
    private static final int DEFAULT_GAP_SCORE = -3;
    
    /** The match score awarded for matches against ambiguous bases. */
    private static final double AMBIGUITY_MATCH_SCORE = 0.75;

    /** The score for a gap in the alignment. */
    private final int gapScore;
    
    /** The substitution matrix to use when scoring matches. */
    private final SubstitutionMatrix<NucleotideGlyph> matrix;

    /**
     * Creates a new <code>SmithWatermanAligner</code>.
     * 
     * @param matrix The {@link SubstitutionMatrix} to use for scoring.
     * @param gapScore The gapping score (penalty) to use.
     */
    public SmithWatermanAligner(SubstitutionMatrix<NucleotideGlyph> matrix, int gapScore)
    {
        super();
        
        this.matrix = matrix;
        this.gapScore = gapScore;
    }

    /**
     * Creates a new <code>SmithWatermanAligner</code> with a default gap penalty 
     * ({@value #DEFAULT_GAP_SCORE}).
     * 
     * @param matrix The {@link SubstitutionMatrix} to use for scoring.
     */
    public SmithWatermanAligner(SubstitutionMatrix<NucleotideGlyph> matrix)
    {
        this(matrix, SmithWatermanAligner.DEFAULT_GAP_SCORE);
    }

    /* (non-Javadoc)
     * @see org.jcvi.align.Aligner#alignSequence(java.lang.CharSequence)
     */
    public Alignment alignSequence(EncodedGlyphs<NucleotideGlyph> querySequence, EncodedGlyphs<NucleotideGlyph> referenceSequence)
    {
        final AlignmentFactory alignment = new AlignmentFactory();
        final ScoringMatrix<NucleotideGlyph> score = new ScoringMatrix<NucleotideGlyph>(referenceSequence, querySequence, this.gapScore);

        /*
         * Set the sequence lengths
         */
        alignment.setQueryLength((int)querySequence.getLength());
        alignment.setReferenceLength((int)referenceSequence.getLength());
        
        /*
         * Evaluate all of the cells.
         */
        score.evaluate(this.matrix);

        final Coordinate start = getAlignmentStartCoordinate(score);
        
        /*
         * Set the alignment start coordinates.
         *    NOTE: We offset by one because we are using 1-indexed residue-based addressing.
         */
        alignment.setQueryBegin(start.x + 1);
        alignment.setReferenceBegin(start.y + 1);
        
        /*
         * Initialize the cursor we'll use to follow the alignment path.
         */
        Coordinate cursor = start;
        
        /*
         * Initialize accumulators for the identity calculation.
         */
        int alignmentLength = 0;
        int identity = 0;
        double matchScore = 0.0;

        // traverse the optimal path and build the alignment strings
        while (cursor.y < referenceSequence.getLength() && cursor.x < querySequence.getLength())
        {
            /*
             * Bump the alignment length counter.
             */
            alignmentLength++;
            
            /*
             * Follow the path recorded in the score matrix.
             */
            switch (score.getPath(cursor))
            {
                case ScoringMatrix.PATH_DIAG:
                    
                    /*
                     * Check for identity.
                     */
                    final NucleotideGlyph refBase = referenceSequence.get(cursor.y);
                    final NucleotideGlyph queryBase = querySequence.get(cursor.x);
                    if (refBase == queryBase)
                    {
                        identity++;
                        matchScore++;
                    }
                    else if (matrix.getScore(refBase, queryBase) > 0)
                    {
                        matchScore += AMBIGUITY_MATCH_SCORE;
                    }
                    
                    cursor = cursor.translate(1, 1);

                    break;

                case ScoringMatrix.PATH_HORZ:
                    
                    alignment.addAbsoluteReferenceGap(cursor.y);
                    cursor = cursor.translate(1, 0);

                    break;

                case ScoringMatrix.PATH_VERT:
                    
                    alignment.addAbsoluteQueryGap(cursor.x);
                    cursor = cursor.translate(0, 1);
                    
                    break;
            }
        }
        
        /*
         * Store the identity and score
         */
        alignment.setIdentity(identity / (double)alignmentLength);
        alignment.setScore(score.getScore(start));
        alignment.setMatch(matchScore / alignmentLength);
        
        /*
         * Update the alignment factory with the alignment stop points
         *     NOTE: This may feel like an off-by-one error since we're using the current 
         *     location of the cursor after it's reached the end of one of the alignment
         *     sequences.  However, we're recording 1-indexed, residue-based addresses, so
         *     the current values are correct when translated backward for the previous
         *     index and then forward for the 1-based index.
         */
        alignment.setQueryEnd(cursor.x);
        alignment.setReferenceEnd(cursor.y);
        
        /*
         * Builld and return a concrete alignment.
         */
        return alignment.build();
    }

    /**
     * Fetches the start coordinate of the alignment within the scoring matrix.
     * 
     * @param score The scoring matrix.
     * @return The {@link Coordinate} of the best start location.
     */
    protected Coordinate getAlignmentStartCoordinate(final ScoringMatrix<NucleotideGlyph> score) 
    {
        return score.getBestStart();
    }
}
