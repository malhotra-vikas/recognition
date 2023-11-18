package com.treenity.image.recognition;

import com.treenity.image.ddb.ScanImageMetadata;
import com.treenity.image.recognition.utils.DependencyFactory;


public class RekognitionMetadataProcessor {
    public static void main(String... args) {
    	String ddbTableName = DependencyFactory.getDdbTableName_default()+"."+DependencyFactory.getCompanyID();
        
        ScanImageMetadata scanImageMetadata = new ScanImageMetadata();
        //scanImageMetadata.scanTable(ddbTableName);
        
        
    	
    }

}
