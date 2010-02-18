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
/*
 * Created on Nov 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public class MultiCasDataStoreFactory implements
        CasDataStoreFactory {

    private final List<CasDataStoreFactory> factories;
    
    public MultiCasDataStoreFactory(CasDataStoreFactory...casNucleotideDataStoreFactories){
        this(Arrays.asList(casNucleotideDataStoreFactories));
    }
    
    public  MultiCasDataStoreFactory(List<CasDataStoreFactory> casNucleotideDataStoreFactories){
        this.factories = new ArrayList<CasDataStoreFactory>(casNucleotideDataStoreFactories );
    }

    @Override
    public NucleotideDataStore getNucleotideDataStoreFor(
            String pathToDataStore) throws CasDataStoreFactoryException {
        for(CasDataStoreFactory factory : factories){
            try{
                return factory.getNucleotideDataStoreFor(pathToDataStore);
            }
            catch(CasDataStoreFactoryException e){
                //ignore error must not be correct format...
            }
        }
       throw new CasDataStoreFactoryException("could not get nucleotide datastore for "+ pathToDataStore);
    }

    @Override
    public QualityDataStore getQualityDataStoreFor(
            String pathToDataStore) throws CasDataStoreFactoryException {
        for(CasDataStoreFactory factory : factories){
            try{
                return factory.getQualityDataStoreFor(pathToDataStore);
            }
            catch(CasDataStoreFactoryException e){
                //ignore error must not be correct format...
            }
        }
       throw new CasDataStoreFactoryException("could not get quality datastore for "+ pathToDataStore);
    }
    
    
}
