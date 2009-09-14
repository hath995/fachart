/**
 *
 * @author Aaron Elligsen
 * 
 */

package FAChart;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * This class ensures only Supreme Commander Forged Alliance replays are loaded.
 * 
 */
public class ReplayFilter extends FileFilter {
    
    public ReplayFilter()
    {
        
    
    }
    
    public boolean accept(File f) 
    {
            if (f.isDirectory()) {
                return true;
            }

            String extension = ReplayFilter.getExtension(f);
            if (extension != null) {
                if (extension.equals("scfareplay")) {
                        return true;
                } else {
                    return false;
                }
            }

            return false;
       }

        //The description of this filter
        public String getDescription() {
            return "SCFAReplay";
        }
        
        public static String getExtension(File f) 
        {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 &&  i < s.length() - 1) 
            {
                ext = s.substring(i+1).toLowerCase();
            }
            return ext;
        }
    }
