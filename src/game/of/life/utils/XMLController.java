package game.of.life.utils;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import game.of.life.Simulation;


public class XMLController {
	public static void loadState(final File file,final Simulation currentSimulation) {
		try {
			final JAXBContext context=JAXBContext.newInstance(XMLSimulation.class);
			 final Unmarshaller um = context.createUnmarshaller();

		        // Reading XML from the file and unmarshalling.
			 final XMLSimulation data = (XMLSimulation) um.unmarshal(file);
		        
		     data.loadSimulation(currentSimulation);
		} catch (final JAXBException e) {
		}
	}
	public static void saveState(final Simulation data,final File file) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (final IOException e) {
			}
		}
		try {
			final JAXBContext context = JAXBContext
			        .newInstance(XMLSimulation.class);
			final Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	        m.marshal(new XMLSimulation(data), file);
		} catch (final JAXBException e) {
			
		}
       

	}
}
