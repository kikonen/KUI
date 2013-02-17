package org.kari.io;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.kari.util.TextUtil;

/**
 * Utilities for file handling
 * 
 * @author kari
 *
 */
public class FileUtil {
    private static final Logger LOG = Logger.getLogger("ki.util.file");
    
    /**
     * Close input/outputstream
     */
    public static void close(Closeable pFile) {
        try {
            if (pFile != null) {
                pFile.close();
            }
        } catch (IOException e) {
            LOG.error("Failed to closec file: " + pFile, e);
        }
    }
    
    /**
     * Load contents of the file
     * 
     * @return RAW byte content of the file
     */
    public static byte[] load(File pFile) 
        throws IOException
    {
        byte[] buffer = new byte[(int)pFile.length()];
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(pFile));
            input.readFully(buffer);
        }  finally {
            close(input);
        }
        return buffer;
    }

    /**
     * Load contents of the file from inputstream
     * 
     * @return RAW byte content of the file
     */
    public static byte[] load(InputStream pInput) 
        throws IOException
    {
        DirectByteArrayOutputStream out = new DirectByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int count = 0;
        while ( (count = pInput.read(buffer)) != -1) {
            out.write(buffer, 0, count);
        }
        return out.toByteArray();
    }

    /**
     * Save pData into file
     * 
     * @param pData RAW byte content of the file
     * @return pFile
     */
    public static File save(File pFile, byte[] pData) 
        throws IOException
    {
        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(pFile));
            output.write(pData);
        }  finally {
            close(output);
        }
        return pFile;
    }

    /**
     * Recursively traverse a directory hierachy and obtain a list of all
     * absolute file names.
     * <p>Regular expression patterns can be provided to explicitly include
     * and exclude certain file names.
     * 
     * @param file the directory whose file hierarchy will be traversed
     * @param included an array of regular expression patterns that will be
     * used to determine which files should be included; or
     * <p><code>null</code> if all files should be included
     * @param excluded an array of regular expression patterns that will be
     * used to determine which files should be excluded; or
     * <p><code>null</code> if no files should be excluded
     * @return the list of absolute file names
     * @since 1.0
     */
    public static List<String> getFileList(File file, Pattern[] included, Pattern[] excluded)
    {
        return getFileList(file, included, excluded, true);
    }
    
    private static List<String> getFileList(
        File file,
        Pattern[] included,
        Pattern[] excluded,
        boolean root)
    {
        List<String> files = new ArrayList<String>();
        if (file.isDirectory())
        {
            String[] list = file.list();
            if (null != list)
            {
                String list_entry;
                for (int i = 0; i < list.length; i++)
                {
                    list_entry = list[i];
                    
                    File next_file = new File(file.getAbsolutePath() + File.separator + list_entry);
                    List<String> dir = getFileList(next_file, included, excluded, false);
                    
                    Iterator<String> dir_it = dir.iterator();
                    String file_name;
                    while (dir_it.hasNext())
                    {
                        file_name = dir_it.next();
                        
                        if (root)
                        {
                            // if the file is not accepted, don't process it further
                            if (!TextUtil.filter(file_name, included, excluded))
                            {
                                continue;
                            }
                            
                        }
                        else
                        {
                            file_name = file.getName() + File.separator + file_name;
                        }
                        
                        int filelist_size = files.size();
                        for (int j = 0; j < filelist_size; j++)
                        {
                            if (((String)files.get(j)).compareTo(file_name) > 0)
                            {
                                files.add(j, file_name);
                                break;
                            }
                        }
                        if (files.size() == filelist_size)
                        {
                            files.add(file_name);
                        }
                    }
                }
            }
        }
        else if (file.isFile())
        {
            String  file_name = file.getName();
            
            if (root)
            {
                if (TextUtil.filter(file_name, included, excluded))
                {
                    files.add(file_name);
                }
            }
            else
            {
                files.add(file_name);
            }
        }
        
        return files;
    }
    
    public static String getExtension(String fileName)
    {
        String  ext = null;
        
        int index = fileName.lastIndexOf('.');
        if (index > 0 &&  index < fileName.length() - 1)
        {
            ext = fileName.substring(index+1).toLowerCase();
        }
        
        return ext;
    }

}
