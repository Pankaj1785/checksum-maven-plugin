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
package net.nicoulaj.maven.plugins.checksum.mojo;

import org.apache.maven.model.FileSet;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Compute specified files checksum digests and store them in individual files and/or a summary file.
 *
 * @author <a href="mailto:julien.nicoulaud@gmail.com">Julien Nicoulaud</a>
 * @since 1.0
 */
@Mojo(
    name = FilesMojo.NAME,
    defaultPhase = LifecyclePhase.VERIFY,
    requiresProject = true,
    inheritByDefault = false,
    threadSafe = true )
public class FilesMojo
    extends AbstractChecksumMojo
{
    /**
     * The mojo name.
     */
    public static final String NAME = "files";

    /**
     * The default file inclusion pattern.
     *
     * @see #getFilesToProcess()
     */
    protected static final String[] DEFAULT_INCLUDES = { "**/**" };

    /**
     * The list of files to process.
     * <p/>
     * <p> Use the following syntax:
     * <pre>&lt;fileSets&gt;
     *   &lt;fileSet&gt;
     *     &lt;directory&gt;...&lt;/directory&gt;
     *     &lt;includes&gt;
     *       &lt;include&gt;...&lt;/include&gt;
     *     &lt;/includes&gt;
     *     &lt;excludes&gt;
     *       &lt;exclude&gt;...&lt;/exclude&gt;
     *     &lt;/excludes&gt;
     *   &lt;/fileSet&gt;
     * &lt;/fileSets&gt;</pre>
     * </p>
     *
     * @since 1.1
     */
    @Parameter( required = true )
    protected List<FileSet> fileSets;

    /**
     * Indicates whether the build will store checksums in separate files (one file per algorithm per artifact).
     *
     * @since 1.0
     */
    @Parameter( defaultValue = "true" )
    protected boolean individualFiles;

    /**
     * The directory where output files will be stored. Leave unset to have each file next to the source file.
     *
     * @since 1.0
     */
    @Parameter
    protected String individualFilesOutputDirectory;

    /**
     * Indicates whether the build will store checksums to a single CSV summary file.
     *
     * @since 1.0
     */
    @Parameter( defaultValue = "true" )
    protected boolean csvSummary;

    /**
     * The name of the summary file created if the option is activated.
     *
     * @see #csvSummary
     * @since 1.0
     */
    @Parameter( defaultValue = "checksums.csv" )
    protected String csvSummaryFile;
    
    /**
     * Indicates whether the build will store checksums to a single CSV summary file.
     *
     * @since 1.3
     */
    @Parameter( defaultValue = "false" )
    protected boolean luaTable;

    /**
     * The name of the summary file created if the option is activated.
     *
     * @see #csvSummary
     * @since 1.3
     */
    @Parameter( defaultValue = "checksums.lua" )
    protected String luaTableFile;

    
    /**
     * The name of the table object of lua script.
     *
     * @see #csvSummary
     * @since 1.3
     */
    @Parameter( defaultValue = "_hubble.contentData" )
    protected String  tableObjectName;
    
    /**
     * Indicates whether the build will store checksums to a single XML summary file.
     *
     * @since 1.0
     */
    @Parameter( defaultValue = "false" )
    protected boolean xmlSummary;

    /**
     * The name of the summary file created if the option is activated.
     *
     * @see #xmlSummary
     * @since 1.0
     */
    @Parameter( defaultValue = "checksums.xml" )
    protected String xmlSummaryFile;

    /**
     * Build the list of files from which digests should be generated.
     *
     * @return the list of files that should be processed.
     */
    protected List<File> getFilesToProcess()
    {
        final List<File> filesToProcess = new ArrayList<File>();
        for ( final FileSet fileSet : fileSets )
        {
            final DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir( fileSet.getDirectory() );
            String[] includes;
            if ( fileSet.getIncludes() != null && !fileSet.getIncludes().isEmpty() )
            {
                final List<String> fileSetIncludes = fileSet.getIncludes();
                includes = fileSetIncludes.toArray( new String[fileSetIncludes.size()] );
            }
            else
            {
                includes = DEFAULT_INCLUDES;
            }
            scanner.setIncludes( includes );

            if ( fileSet.getExcludes() != null && !fileSet.getExcludes().isEmpty() )
            {
                final List<String> fileSetExcludes = fileSet.getExcludes();
                scanner.setExcludes( fileSetExcludes.toArray( new String[fileSetExcludes.size()] ) );
            }

            scanner.addDefaultExcludes();

            scanner.scan();

            for ( String filePath : scanner.getIncludedFiles() )
            {
                filesToProcess.add( new File( fileSet.getDirectory(), filePath ) );
            }
        }

        return filesToProcess;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isIndividualFiles()
    {
        return individualFiles;
    }

    /**
     * {@inheritDoc}
     */
    protected String getIndividualFilesOutputDirectory()
    {
        return individualFilesOutputDirectory;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isCsvSummary()
    {
        return csvSummary;
    }

    /**
     * {@inheritDoc}
     */
    protected String getCsvSummaryFile()
    {
        return csvSummaryFile;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isLuaTable()
    {
        return luaTable;
    }

    /**
     * {@inheritDoc}
     */
    protected String getLuaTableFile()
    {
        return luaTableFile;
    }
    
    protected String getTableObjectName()
    {
    	return tableObjectName;
    }
    /**
     * {@inheritDoc}
     */
    protected boolean isXmlSummary()
    {
        return xmlSummary;
    }

    /**
     * {@inheritDoc}
     */
    protected String getXmlSummaryFile()
    {
        return xmlSummaryFile;
    }
}
