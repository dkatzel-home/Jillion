package org.jcvi.common.core.seq.fastx.fastq;

import java.io.File;
import java.io.IOException;
import static org.junit.Assert.*;
public class TestIndexedFastqFileDataStoreGuessCodec extends AbstractTestFastQFileDataStore{

    @Override
    protected FastqDataStore createFastQFileDataStore(File file,
            FastqQualityCodec qualityCodec) throws IOException {
    	FastqQualityCodec codec = FastqUtil.guessQualityCodecUsed(file);
    	assertSame(codec, qualityCodec);
        return IndexedFastqFileDataStore.create(file, codec);
    }

}
