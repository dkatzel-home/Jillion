/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.internal.core.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jcvi.jillion.core.io.BufferSize;
import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.io.OutputStreams;

public final class OutputStreamFactory {

    /**
     * Create an appropriate OutputSream for the given based on the
     * file extension.
     * Currently Supports (ignoring case):
     * <ul>
     * <li>gz - GZIP</li>
     * <li>zip - Zip</li>
     * </ul>
     * 
     * Anything else will return a normal buffered outputStream.
     * @param file the file to write to; can not be null. 
     * @return a new outputStream.
     * @throws IOException if there is a problem creating any intermediate directories or the output stream.
     * @throws NullPointerException if file is null.
     */
    public static OutputStream create(File file) throws IOException{
       return create(file, BufferSize.kb(8));
    }
    /**
     * Create an appropriate OutputSream for the given based on the
     * file extension.
     * Currently Supports (ignoring case):
     * <ul>
     * <li>gz - GZIP</li>
     * <li>zip - Zip</li>
     * </ul>
     * 
     * Anything else will return a normal buffered outputStream.
     * @param file the file to write to; can not be null. 
     * @return a new outputStream.
     * @throws IOException if there is a problem creating any intermediate directories or the output stream.
     * @throws NullPointerException if file is null.
     */
    public static OutputStream create(File file, int bufferSize) throws IOException{
        IOUtil.mkdirs(file.getParentFile());
        
        String extension = FileUtil.getExtension(file);
        
        if("gz".equalsIgnoreCase(extension)){
            //gzip
            return OutputStreams.gzip(file, bufferSize);
        }
        if("zip".equalsIgnoreCase(extension)){
            return OutputStreams.zip(file, bufferSize);
        }
        return new BufferedOutputStream(new FileOutputStream(file), bufferSize);
    }
}
