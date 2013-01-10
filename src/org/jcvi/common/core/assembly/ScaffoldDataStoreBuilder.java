package org.jcvi.common.core.assembly;

import org.jcvi.common.core.util.Builder;
import org.jcvi.jillion.core.DirectedRange;

public interface ScaffoldDataStoreBuilder extends Builder<ScaffoldDataStore>{

	ScaffoldDataStoreBuilder addScaffold(Scaffold scaffold);
	
	ScaffoldDataStoreBuilder addPlacedContig(String scaffoldId, String contigId, DirectedRange directedRange);
}
