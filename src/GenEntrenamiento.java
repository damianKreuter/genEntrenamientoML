import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class GenEntrenamiento {
	
	public static final int ROTATE_LEFT = 1;
	public static final int ROTATE_RIGHT = -1;
	
	public static void main(String args[]) {
		System.out.println("Generacion de imagenes ha comenzado");
		
		String pathImagenes = args[0];
		
		Path pathDeImagenes = Paths.get(pathImagenes);
		
		try(Stream<Path> subPaths = Files.walk(pathDeImagenes, 1)){
			
		//	subPaths.filter(Files::isRegularFile).forEach(System.out::println);
			List<File> imagenes = subPaths.filter(Files::isRegularFile)
					.map(Path::toFile)
					.collect(Collectors.toList());
			int totalImagenes = imagenes.size();
			int index = 1;
			float porcentaje = (float) index/totalImagenes * 100;
			for(File imagen : imagenes) {
				porcentaje = (float) index/totalImagenes  * 100;
				System.out.println(index+" de "+totalImagenes + " | "+porcentaje+"%");
				System.out.println("Archivo: "+imagen.getName());
				rotarImagen3Veces(imagen, pathImagenes);
				index++;
			}
			System.out.println("FINALIZADO");
		}
		/*s
		 * Del path IMAGENES obtiene las imagenes las cuales va a rotar 3 veces
		 * 3 de estas imagenes serán usadas para entrenamiento y la imagen 
		 * original será utilizada para validación
		 */ 
		catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void rotarImagen3Veces(File input, String basePath) throws IOException {
		String directoryName = basePath.concat("/Entrenamiento");

	    File directory = new File(directoryName);
	    if (!directory.exists()){
	        directory.mkdir();
	        // If you require it to make the entire directory path including parents,
	        // use directory.mkdirs(); here instead.
	    }
		
		String path = input.getAbsolutePath() + "/entrenamiento";
		File outputL = new File(directoryName + "/" + input.getName().substring(0, input.getName().length()-4) +"IZQ.jpg");
		File outputD = new File(directoryName + "/" + input.getName().substring(0, input.getName().length()-4) +"DER.jpg");
		File outputT = new File(directoryName + "/" + input.getName().substring(0, input.getName().length()-4) +"VOL.jpg");
		rotacion90Grados(input, outputL, ROTATE_LEFT);
		rotacion90Grados(input, outputD, ROTATE_RIGHT);
		rotacion180Grados(input, outputT);
	}
	
	public static void rotacion90Grados(File input, File output, int direction) throws IOException {
			ImageInputStream streamImage = ImageIO.createImageInputStream(input);
			Iterator<ImageReader> iterator = ImageIO.getImageReaders(streamImage);
			ImageReader reader = iterator.next();
			String format = reader.getFormatName();
			
			BufferedImage image = ImageIO.read(streamImage);
			int width = image.getWidth();
			int height = image.getHeight();
			
			BufferedImage rotar = new BufferedImage(height, width, image.getType());
			
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					switch(direction) {
					case ROTATE_LEFT:
						rotar.setRGB(y, (width-1) - x, image.getRGB(x, y));
						break;
					case ROTATE_RIGHT:
						rotar.setRGB((height-1) - y, x, image.getRGB(x, y));
					}
				}
			}
			ImageIO.write(rotar, format, output);
	}
	
	public static void rotacion180Grados(File input, File output) throws IOException {
		ImageInputStream streamImage = ImageIO.createImageInputStream(input);
		Iterator<ImageReader> iterator = ImageIO.getImageReaders(streamImage);
		ImageReader reader = iterator.next();
		String format = reader.getFormatName();
			
		BufferedImage image = ImageIO.read(streamImage);
		int width = image.getWidth();
		int height = image.getHeight();
			
		BufferedImage rotar = new BufferedImage(width, height, image.getType());
			
		for(int y = 0; y< height; y++) {
			for(int x = 0; x < width; x++) {
				rotar.setRGB((width-1) - x, (height-1) - y, image.getRGB(x, y));
			}
		}
		ImageIO.write(rotar, format, output);
	}
}
