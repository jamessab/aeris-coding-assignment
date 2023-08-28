package com.aeris.assignment.aeriscodingassignment;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ucar.ma2.Array;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;
import ucar.nc2.dt.image.*;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Handles all operations related to the concentration netCDF file
 */
@Component("netCDFBean")
public class NetCDFBean {
    Logger logger = LoggerFactory.getLogger(NetCDFBean.class);

    @Autowired JsonDataBean jsonDataBean;

    @Value("${spring.aeris.ncfilename}")
    private String ncFilename;

    private NetcdfFile ncFile;  // The opened netCDF file

    // The following variables are declared at the class level since these won't change after startup.
    // Validate them and hold a reference to them during startup.
    private Variable xVariable;
    private Variable yVariable;
    private Variable concentrationVariable;

    /**
     * Initializes the bean and validates that we have all of the data we need
     */
    public void openNetCDFFile() throws Exception
    {
        String ncFilePath;
        URL url = getClass().getClassLoader().getResource(ncFilename);
        if (url == null || !Files.exists(Path.of(url.getPath()))) {
            // We can't find the nc file. When running from inside a jar file this is possible
            // since the file might only reside inside the jar and not have a true file path.
            // In that case, we would usually just read it as a resource stream but the "open"
            // in NetcdfFile requires a string filepath. We have tried to copy it to the tmp directory
            // during deployment so check there next.

            logger.info(String.format("Couldn't find the file: '%s'. Trying the temp directory.", url));

            String tmpdir = System.getProperty("java.io.tmpdir");
            if (!Files.exists(Path.of(tmpdir, ncFilename))) {
                logger.error(String.format("Couldn't find the file: '%s' in the temp directory. Initialization failed.", Files.exists(Path.of(tmpdir, ncFilename))));
                throw new Exception(String.format("Couldn't find the NC file '%s'. Aborting startup.", ncFilename));
            }
            ncFilePath = Path.of(tmpdir, ncFilename).toString();
        }
        else {
            ncFilePath = url.getPath();
        }
        ncFile = NetcdfFiles.open(ncFilePath);

        // Since we currently don't reload the configuration file during runtime, do all the validation now
        // to save some cycles later since if the needed variables don't exist we can fast fail here.
        // Any exceptions thrown here will result in an invalid bean exception being created and the
        // startup process will abort
        if (ncFile == null) {
            throw new Exception("Error opening the 'concentration' file. Aborting startup.");
        }

        concentrationVariable = ncFile.findVariable("concentration");
        if (concentrationVariable == null) {
            throw new Exception(String.format("Variable 'concentration' not found in the nc file '%s'. Aborting startup.", ncFilename));
        }

        xVariable = ncFile.findVariable("x");
        if (xVariable == null) {
            throw new Exception(String.format("Variable 'x' not found in the nc file '%s'. Aborting startup.", ncFilename));
        }

        yVariable = ncFile.findVariable("y");
        if (yVariable == null) {
            throw new Exception(String.format("Variable 'y' not found in the nc file '%s'. Aborting startup.", ncFilename));
        }
    }

    /**
     * Returns the detailed info of the NC file
     *
     * @return detailed info of the NC file
     */
    public String getInfo() {
        return ncFile.getDetailInfo();
    }

    /**
     * Returns a json response that includes x, y, and concentration data.
     *
     * @param  timeIndex the timeIndex to retrieve for the concentration. Valid range = 0 to number of time samples
     * @param  zIndex the zIndex to retrieve for the concentration. Valid range = 0 to number of z samples
     * @return json string containing the x, y and concentration data
     */
    public String getData(int timeIndex, int zIndex) throws Exception {
        Validate.isTrue( timeIndex >= 0 && timeIndex < concentrationVariable.getShape()[0],
                String.format("Invalid parameter. timeIndex must be between 0 and %d",  concentrationVariable.getShape()[0] - 1));

        Validate.isTrue( zIndex >= 0 && zIndex < concentrationVariable.getShape()[1],
                String.format("Invalid parameter. zIndex must be between 0 and %d",  concentrationVariable.getShape()[1] - 1));

        Array xArray = xVariable.read();
        Array yArray = yVariable.read();
        Array concentrationArray = concentrationVariable.read(String.format("%d, %d, :, :", timeIndex, zIndex)).reduce();

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        jsonDataBean.setValues(xArray.getStorage(), yArray.getStorage(), concentrationArray.getStorage());
        return ow.writeValueAsString(jsonDataBean);
    }

    /**
     * Returns a PNG image of the concentration data.
     *
     * @param  timeIndex the timeIndex to retrieve for the concentration. Valid range = 0 to number of time samples
     * @param  zIndex the zIndex to retrieve for the concentration. Valid range = 0 to number of z samples
     * @return png image of the concentration data
     */
    public byte[] getImage(int timeIndex, int zIndex) throws Exception {
        Validate.isTrue( timeIndex >= 0 && timeIndex < concentrationVariable.getShape()[0],
                String.format("Invalid parameter. timeIndex must be between 0 and %d", concentrationVariable.getShape()[0] - 1));

        Validate.isTrue( zIndex >= 0 && zIndex < concentrationVariable.getShape()[1],
                String.format("Invalid parameter. zIndex must be between 0 and %d", concentrationVariable.getShape()[1] - 1));

        // Get the subset of concentration that was requested in the parameters.
        Array concentrationArray = ncFile.readSection(String.format("concentration(%d, %d, :, :)", timeIndex, zIndex)).reduce();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageOutputStream ios =  ImageIO.createImageOutputStream(baos);
        BufferedImage bi = ImageArrayAdapter.makeGrayscaleImage(concentrationArray, null);
        ImageIO.write(bi, "png", ios);
        return baos.toByteArray();
    }
}

