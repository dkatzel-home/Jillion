package org.jcvi.jillion.align.pairwise;

import org.jcvi.jillion.align.pairwise.blosom.AllBlosomUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	AllBlosomUnitTests.class,
    	TestNucleotideSmithWatermanAligner.class,    	
    	TestAminoAcidSmithWaterman.class,
    	
    	TestNucleotideNeedlemanWunschAligner.class,
    	TestAminoAcidNeedlemanWunschAligner.class
    }
    )
public class AllPairwiseUnitTests {

}