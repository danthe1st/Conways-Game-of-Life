package game.of.live.utils;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import game.of.live.Simulation;


public class XMLController {
	public static void loadState(File file,Simulation currentSimulation) {
		try {
			JAXBContext context=JAXBContext.newInstance(XMLSimulation.class);
			 Unmarshaller um = context.createUnmarshaller();

		        // Reading XML from the file and unmarshalling.
			 XMLSimulation data = (XMLSimulation) um.unmarshal(file);
		        
		     data.loadSimulation(currentSimulation);
		} catch (JAXBException e) {
		}
	}
	public static void saveState(Simulation data,File file) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
		}
		try {
			JAXBContext context = JAXBContext
			        .newInstance(XMLSimulation.class);
			Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	        m.marshal(new XMLSimulation(data), file);
		} catch (JAXBException e) {
			
		}
       

	}
}
