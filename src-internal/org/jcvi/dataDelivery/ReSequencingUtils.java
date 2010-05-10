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
 * Created on Oct 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.dataDelivery;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.jcvi.auth.DefaultJCVIAuthorizer;
import org.jcvi.auth.JCVIAuthorizer;
import org.jcvi.http.HttpGetRequestBuilder;

public final class ReSequencingUtils {

    public enum OUTPUT_TYPE{
        CSV("csv"),
        EXCEL ("xls")
        ;
        private String type;
        private OUTPUT_TYPE(String type){
            this.type= type;
        }
        
        public String getType(){
            return type;
        }
    }
    private static final String GET_MANIFEST_URL ="http://reports-prod:8280/JasperServer-Pro/flow.html";
    
    public static InputStream getManifestFor(String sequencingPlateBarcode, JCVIAuthorizer auth,OUTPUT_TYPE outputType) throws IOException{
        if(sequencingPlateBarcode ==null || sequencingPlateBarcode.trim().isEmpty()){
            throw new IllegalArgumentException("must provide at least one sequencing plate barcode");
        }
        HttpGetRequestBuilder builder = new HttpGetRequestBuilder(GET_MANIFEST_URL);
        builder.addVariable("_flowId", "viewReportFlow")
        .addVariable("reportUnit", "/Secured_Details/Resequencing/QC/Reseq_Manifest")
        .addVariable("standAlone", "true")
        .addVariable("ParentFolderUri", "/Secured_Details/Resequencing/QC")
        .addVariable("j_username", auth.getUsername())
        .addVariable("j_password", new String(auth.getPassword()))
        .addVariable("SearchBy", "sequencing_plate_barcode")
        .addVariable("SearchFor", sequencingPlateBarcode)
        .addVariable("output", outputType.getType());
        
        HttpURLConnection connection = builder.build();
        return connection.getInputStream();
    }
    
    public static InputStream getManifestFor(String Platebarcode,OUTPUT_TYPE outputType) throws IOException{
       return getManifestFor(Platebarcode, DefaultJCVIAuthorizer.JOE_USER, outputType);
    }
    
   
}
