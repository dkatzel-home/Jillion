package org.jcvi.jillion.assembly.asm;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.asm.AsmContigDataStore;
import org.jcvi.jillion.assembly.asm.DefaultAsmContigDataStore;
import org.jcvi.jillion.trace.frg.FragmentDataStore;

public class TestDefaultAsmContigDataStore extends AbstractTestAsmContigDataStore{

	@Override
	protected AsmContigDataStore createDataStore(File asmFile,
			FragmentDataStore frgDataStore) throws IOException {
		return DefaultAsmContigDataStore.createDataStore(asmFile, frgDataStore);
	}

	
}