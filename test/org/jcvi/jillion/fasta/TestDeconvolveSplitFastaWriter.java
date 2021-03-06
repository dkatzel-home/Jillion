/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecordBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaWriter;
import org.jcvi.jillion.fasta.nt.NucleotideFastaWriterBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public class TestDeconvolveSplitFastaWriter {

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private NucleotideFastaDataStore datastore;
	
	@Before
	public void setupDataStore() throws IOException{
		ResourceHelper helper = new ResourceHelper(TestDeconvolveSplitFastaWriter.class);
		
		datastore = new NucleotideFastaFileDataStoreBuilder(helper.getFile("nt/files/seqs.fasta"))
							.build();
	}
	private String getSequenceDirectionFor(String seqname){
		if(seqname.endsWith("F")){
			return "forward";
		}
		return "reverse";
	}
	private NucleotideFastaWriter create(){
		
		 return  SplitFastaWriter.deconvolve(NucleotideFastaWriter.class, 
					record-> getSequenceDirectionFor(record.getId()),
					dir -> new NucleotideFastaWriterBuilder(new File(tmpDir.getRoot(), dir + ".fasta"))
											.build());
	}
	private File getForwardFile(){
		return new File(tmpDir.getRoot(), "forward.fasta");
	}
	private File getReverseFile(){
		return new File(tmpDir.getRoot(), "reverse.fasta");
	}
	
	@Test
	public void nullDeconvolveReturnValueShouldNotWriteRecord() throws DataStoreException, IOException{
	   
	   try(NucleotideFastaWriter writer = SplitFastaWriter.deconvolve(NucleotideFastaWriter.class, 
                   record-> null,
                   dir -> new NucleotideFastaWriterBuilder(new File(tmpDir.getRoot(), dir + ".fasta"))
                                                                   .build());
                   StreamingIterator<NucleotideFastaRecord> iter = datastore.iterator();
           ){
                   while(iter.hasNext()){
                           writer.write(iter.next());
                   }
           }
	   //since we told it to skip everything no files should be written
	   assertEquals(0, tmpDir.getRoot().listFiles().length);
	   
	}
	@Test
        public void callingCloseAgainIsNoOp() throws IOException{
               NucleotideFastaWriter writer= create();
               writer.close();
               writer.close();
        }
	@Test
	public void donotCreateFilesIfNothingToWrite() throws IOException{
		create()
		.close();
		
		assertFalse(getForwardFile().exists());
		assertFalse(getReverseFile().exists());
	}
	
	@Test
	public void tryingToWriteRecordAfterCloseShouldThrowIOException() throws IOException, DataStoreException{
		NucleotideFastaWriter writer = create();
		writer.close();
		
		expectedException.expect(IOException.class);
		expectedException.expectMessage("closed");
		
		writer.write(datastore.get("IWKNA01T07A01PB2A1101R"));
	}
	
	@Test
	public void correctlyDeconvolve() throws IOException, DataStoreException{
		try(NucleotideFastaWriter writer = create();
			StreamingIterator<NucleotideFastaRecord> iter = datastore.iterator();
		){
			while(iter.hasNext()){
				writer.write(iter.next());
			}
		}
		
		
		NucleotideFastaDataStore expectedForward = new NucleotideFastaFileDataStoreBuilder(getForwardFile()).build();
		NucleotideFastaDataStore expectedReverse = new NucleotideFastaFileDataStoreBuilder(getReverseFile()).build();
		
		assertEquals(1, expectedForward.getNumberOfRecords());
		assertEquals(1, expectedReverse.getNumberOfRecords());
		
		assertEquals(datastore.get("IWKNA01T07A01PB2A1F"), expectedForward.get("IWKNA01T07A01PB2A1F"));
		assertEquals(datastore.get("IWKNA01T07A01PB2A1101R"), expectedReverse.get("IWKNA01T07A01PB2A1101R"));
	}
	
	@Test
	public void gettingSameDeconvoleKeyBackShouldNotMakeNewFile() throws IOException, DataStoreException{
	    
	    NucleotideFastaWriter writer = create();
            NucleotideFastaRecord record1 = new NucleotideFastaRecordBuilder("id_F", NucleotideSequenceTestUtil.create("ACGT"))
                                                    .comment("a comment")
                                                    .build();
            NucleotideFastaRecord record2 = new NucleotideFastaRecordBuilder("id2_F", NucleotideSequenceTestUtil.create("ACGT")).build();
            
            
            writer.write(record1.getId(), record1.getSequence(), record1.getComment());
            writer.write(record2.getId(), record2.getSequence());
            
            writer.close();
            
            
            assertFalse(getReverseFile().exists());
            NucleotideFastaDataStore expectedForward = new NucleotideFastaFileDataStoreBuilder(getForwardFile()).build();
            
            assertEquals(record1, expectedForward.get(record1.getId()));
            assertEquals(record2, expectedForward.get(record2.getId()));
            
	}
}
