/*
 * Copyright 2010-2012 Julien Nicoulaud <julien.nicoulaud@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.nicoulaj.maven.plugins.checksum.execution.target;

import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An {@link ExecutionTarget} that writes digests to a CSV file.
 *
 * @author <a href="mailto:julien.nicoulaud@gmail.com">Julien Nicoulaud</a>
 * @since 1.0
 */
public class LuaTableFileTarget
    implements ExecutionTarget
{
    /**
     * The line separator character.
     */
    public static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

    /**
     * The CSV column separator character.
     */
    public static final String KEY_VALUE_SEPARATOR = " = ";

    /**
     * The CSV comment marker character.
     */
    public static final String CSV_COMMENT_MARKER = "#";

    /**
     * Encoding to use for generated files.
     */
    protected String encoding;

    /**
     * table object name
     */
    protected String tableObjectName;

    
    /**
     * The association file => (algorithm,hashcode).
     */
    protected Map<File, Map<String, String>> filesHashcodes;

    /**
     * The set of algorithms encountered.
     */
    protected SortedSet<String> algorithms;

    /**
     * The target file where the summary is written.
     */
    protected File summaryFile;

    /**
     * Build a new instance of {@link CsvSummaryFileTarget}.
     *
     * @param summaryFile the file to which the summary should be written.
     * @param encoding    the encoding to use for generated files.
     */
    public LuaTableFileTarget( File summaryFile, String encoding,String tableObjectName )
    {
        this.summaryFile = summaryFile;
        this.encoding = encoding;
        this.tableObjectName = tableObjectName;
    }

    /**
     * {@inheritDoc}
     */
    public void init()
    {
        filesHashcodes = new HashMap<File, Map<String, String>>();
        algorithms = new TreeSet<String>();
    }

    /**
     * {@inheritDoc}
     */
    public void write( String digest, File file, String algorithm )
    {
        // Initialize an entry for the file if needed.
        if ( !filesHashcodes.containsKey( file ) )
        {
            filesHashcodes.put( file, new HashMap<String, String>() );
        }

        // Store the algorithm => hashcode mapping for this file.
        Map<String, String> fileHashcodes = filesHashcodes.get( file );
        fileHashcodes.put( algorithm, digest );

        // Store the algorithm.
        algorithms.add( algorithm );
    }

    /**
     * {@inheritDoc}
     */
    public void close()
        throws ExecutionTargetCloseException
    {
        StringBuilder sb = new StringBuilder();

        // Write the CSV file header.
       
        
        

        // Write a line for each file.
        for ( File file : filesHashcodes.keySet() )
        {
            Map<String, String> fileHashcodes = filesHashcodes.get( file );
            for ( String algorithm : algorithms )
            {
            	 sb.append(tableObjectName).append("[");
            	sb.append("\'"+file.getParentFile().getName()+"/"+ file.getName() +"\'").append("]");
                sb.append( KEY_VALUE_SEPARATOR );
                if ( fileHashcodes.containsKey( algorithm ) )
                {
                    sb.append("\'"+fileHashcodes.get( algorithm )+"\'" );
                }
                else
                {
                	sb.append("\'\'" );
                }
                sb.append( LINE_SEPARATOR );
            }
            
        }
       
       
        // Make sure the parent directory exists.
        FileUtils.mkdir( summaryFile.getParent() );

        // Write the result to the summary file.
        try
        {
            FileUtils.fileWrite( summaryFile.getPath(), encoding, sb.toString() );
        }
        catch ( IOException e )
        {
            throw new ExecutionTargetCloseException( e.getMessage() );
        }
    }
}
