/*   Copyright (c) INRIA, 2015. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class FOWOptions {

    @Option(name = "-bw", aliases = {"--block-width"}, usage = "clustered view block width")
    public int blockWidth = 400;

    @Option(name = "-bh", aliases = {"--block-height"}, usage = "clustered view block height")
    public int blockHeight = 300;

    @Option(name = "-r", aliases = {"--num-rows"}, usage = "number of rows in the clustered view")
    public int numRows = 2;

    @Option(name = "-c", aliases = {"--num-cols"}, usage = "number of columns in the clustered view")
    public int numCols = 2;

    @Option(name = "-mw", aliases = {"--mullion-width"}, usage = "mullions width")
    public int mullionWidth = 0;

    @Option(name = "-mh", aliases = {"--mullion-height"}, usage = "mullions height")
    public int mullionHeight = 0;

    @Option(name = "-fs", aliases = {"--fullscreen"}, usage = "full-screen")
    public boolean fullscreen = false;

    @Option(name = "-noaa", usage = "disable anti-aliasing")
    public boolean noaa = false;

    @Option(name = "-fits", aliases = {"--fits-files"}, usage = "FITS files")
    public String fits_file_names = null;

    @Option(name = "-pdf", aliases = {"--pdf-file"}, usage = "PDF file")
    public String pdf_file_name = null;

    @Option(name = "-zfits", aliases = {"--zuist-fits-file"}, usage = "ZUIST FITS file")
    public String path_to_zuist_fits = null;

    @Option(name = "-smarties", usage = "enable smarties")
    public boolean smarties = false;

    @Option(name = "-ldDir", aliases = {"--local-data-dir"}, usage = "FITS/PDF/... download directory")
    public String path_to_data_dir = "/tmp";

    @Option(name = "-IP", aliases = {"--local-http-ip"}, usage = "IP of NanoHTTPD server used to share downloaded FITS/PDF/... data")
    public String httpdIP = Config.HTTPD_IP;

    @Option(name = "-port", aliases = {"--local-http-port"}, usage = "port of NanoHTTPD server used to share downloaded FITS/PDF/... data")
    public int httpdPort = Config.HTTPD_PORT;

    @Argument
    List<String> arguments = new ArrayList<String>();

    public boolean standalone = true; //not a CLI option

}
