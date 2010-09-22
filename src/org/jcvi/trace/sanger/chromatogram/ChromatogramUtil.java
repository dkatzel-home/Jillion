/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
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

package org.jcvi.trace.sanger.chromatogram;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.List;

import org.jcvi.Builder;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.IOUtil;
import org.jcvi.io.MagicNumberInputStream;
import org.jcvi.sequence.Peaks;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.abi.Ab1FileParser;
import org.jcvi.trace.sanger.chromatogram.abi.AbiUtil;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramFileParser;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramFileParser;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRUtil;

/**
 * {@code ChromatogramUtil} is a utility class for working with {@link Chromatogram}s.
 * @author dkatzel
 *
 *
 */
public  final class ChromatogramUtil {
    
    private ChromatogramUtil(){}
    private static final short[] FALSE_WAVEFORM = { 21, 74, 202, 441, 769, 1073, 1200, 1073, 769, 441, 202, 74, 21 };
    /**
     * {@code FakeChannelGroupBuilder} is a {@link Builder} that can build
     * a new {@link ChannelGroup} instance which contains
     * fake (made up) wave form information.  This is useful if chromatograms
     * need to be generated from data where the original waveform data is lost.
     * @author dkatzel
     * @author aresnick
     *
     *
     */
    public static final class FakeChannelGroupBuilder implements Builder<ChannelGroup>{

        
        
        private final EncodedGlyphs<NucleotideGlyph> basecalls;
        private final Peaks peaks;
        private final EncodedGlyphs<PhredQuality> qualities;
        /**
         * Construct a new FakeChannelGroupBuilder which will
         * use the given parameters to fake waveform data.
         * @param basecalls the called basecalls for this fake channel group.
         * @param qualities the quality values for this fake channel group.
         * @param peaks the peak position data for this fake cahnnel group.
         * 
         * @throws NullPointerException if any of the arguments are null
         * @throws IllegalArgumentException if basecalls, qualities
         * and peaks are not all the same length
         */
        public FakeChannelGroupBuilder(EncodedGlyphs<NucleotideGlyph> basecalls,
                EncodedGlyphs<PhredQuality> qualities, Peaks peaks) {
            if(basecalls.getLength() != qualities.getLength()){
                throw new IllegalArgumentException("basecalls must be same length as qualities");
            }
            if(basecalls.getLength() != peaks.getData().getLength()){
                throw new IllegalArgumentException("basecalls must be same length as peaks");
            }
            if(qualities.getLength() != peaks.getData().getLength()){
                throw new IllegalArgumentException("qualities must be same length as peaks");
            }
            this.basecalls = basecalls;
            this.qualities = qualities;
            this.peaks = peaks;
        }
        
        @Override
        public ChannelGroup build() {
            List<NucleotideGlyph> bases = basecalls.decode();
            // build bogus confidence arrays
            //automatically filled with 0s
            byte[] aConfidence = new byte[bases.size()];
            byte[] cConfidence = new byte[bases.size()];
            byte[] gConfidence = new byte[bases.size()];
            byte[] tConfidence = new byte[bases.size()];
            
            // build bogus signal waveforms
            //automatically filled with 0s
            short[] aSignal = new short[(FALSE_WAVEFORM.length-1)*(bases.size()+1)];
            short[] cSignal = new short[(FALSE_WAVEFORM.length-1)*(bases.size()+1)];
            short[] tSignal = new short[(FALSE_WAVEFORM.length-1)*(bases.size()+1)];
            short[] gSignal = new short[(FALSE_WAVEFORM.length-1)*(bases.size()+1)];
            
            short[] peakLocations = ShortGlyph.toArray(peaks.getData().decode());
            byte[] qualityValues =PhredQuality.toArray(this.qualities.decode());
            for(int i=0; i<peakLocations.length; i++){
                NucleotideGlyph basecall = bases.get(i);
                short peakLocation = peakLocations[i];
                byte qualityValue = qualityValues[i];
                switch (basecall) {
                    case Adenine:
                        createFakeChannelDataFor(aConfidence, qualityValue, i, aSignal, peakLocation);
                        break;
                    case Cytosine:  
                        createFakeChannelDataFor(cConfidence, qualityValue, i, cSignal, peakLocation);
                        break;
                    case Guanine:
                        createFakeChannelDataFor(gConfidence, qualityValue, i, gSignal, peakLocation);   
                        break;
                    case Thymine:
                        createFakeChannelDataFor(tConfidence, qualityValue, i, tSignal, peakLocation);   
                        break;
                }
            }
            return new DefaultChannelGroup(
                    new Channel(aConfidence,aSignal),
                    new Channel(cConfidence,cSignal),
                    new Channel(gConfidence,gSignal),
                    new Channel(tConfidence,tSignal));
        }
        
        private void createFakeChannelDataFor(byte[] confidenceChannel, byte qualityValue,
                int channelOffset,short[] signalChannel, short peakLocation ){
            confidenceChannel[channelOffset] = qualityValue;
            addWaveformTo(signalChannel,peakLocation);  
        }
        
        private void addWaveformTo(short[] signal, short peakLocation) {
            int waveStart = peakLocation - (FALSE_WAVEFORM.length-1)/2;
            for ( int i = 0; i < FALSE_WAVEFORM.length; i++ ) {
                signal[waveStart+i] += FALSE_WAVEFORM[i];
            }
        }
    }
    /**
     * Create a new instance of Peaks that are equally spaced apart.
     * @param numberOfPeaks the number of peaks to make.
     * @param numberOfPositionsBetweenEachPeak the number of positions
     * apart to space each peak.
     * @return a new {@link Peaks} instance.
     * @throws IllegalArgumentException if numberOfPeaks is {@code <0} or numberOfPositionsBetweenEachPeak
     * is {code <1}.
     */
    public static  Peaks buildFakePeaks(int numberOfPeaks, int numberOfPositionsBetweenEachPeak){
        if(numberOfPositionsBetweenEachPeak<1){
            throw new IllegalArgumentException("number of positions between each peak must be > 0");
        }
        ShortBuffer fakePeaks = ShortBuffer.allocate(numberOfPeaks);
        for ( int i = 0; i < numberOfPeaks; i++ ) {
            fakePeaks.put((short)((numberOfPositionsBetweenEachPeak)*(i+1)));
        }
        return new Peaks(fakePeaks);
    }
    /**
     * Create a new instance of Peaks that are equally spaced apart
     * using the default space between each peak.
     * @param numberOfPeaks the number of peaks to make.
     * @return a new {@link Peaks} instance.
     * @throws IllegalArgumentException if numberOfPeaks is {@code <0}
     */
    public static  Peaks buildFakePeaks(int numberOfPeaks) {
        return buildFakePeaks(numberOfPeaks, FALSE_WAVEFORM.length-1);
    }
    public static void parseChromatogram(File chromatogramFile, ChromatogramFileVisitor visitor) throws IOException, TraceDecoderException{
        if(visitor ==null){
            throw new NullPointerException("visitor can not be null");
        }
        InputStream fileInputStream =null;
        try{
            fileInputStream = new FileInputStream(chromatogramFile);
            parseChromatogram(fileInputStream, visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(fileInputStream);
        }
    }
        
    public static void parseChromatogram(InputStream in, ChromatogramFileVisitor visitor) throws IOException, TraceDecoderException{
        MagicNumberInputStream mIn = new MagicNumberInputStream(in);
        byte[] magicNumber = mIn.peekMagicNumber();
        if(Arrays.equals(AbiUtil.MAGIC_NUMBER, magicNumber)){
            Ab1FileParser.parseAb1File(mIn, visitor);
        }else if(Arrays.equals(ZTRUtil.ZTR_MAGIC_NUMBER, magicNumber)){
            ZTRChromatogramFileParser.parseZTRFile(mIn, visitor);
        }else{
            SCFChromatogramFileParser.parseSCFFile(mIn, visitor);
        }
    }
}
