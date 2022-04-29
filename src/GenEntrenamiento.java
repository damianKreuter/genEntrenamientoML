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
	
	public static final int FLIP_HORIZONTAL = 1;
	public static final int FLIP_VERTICAL = 2;
	
	public static void main(String args[]) {
		System.out.println("Generacion de imagenes a ROTAR ha comenzado");
		int size = args.length;
		String pathBase = args[0];
		List<String> nombreDirectorios = new ArrayList<>();
		
		Path pathDeImagenes = Paths.get(pathBase);
		try(Stream<Path> subPaths = Files.walk(pathDeImagenes, 1)){
			nombreDirectorios = subPaths.filter(Files::isDirectory)
				.map(Path::toFile)
				.map(File::getName)
				.collect(Collectors.toList());
			nombreDirectorios.remove(0);
		} catch (IOException e) {
			System.out.println("ERROR: "+e.getMessage());
			e.printStackTrace();
		}
		
		int totalImagenes = obtenerTotalImagenes(pathBase, nombreDirectorios);
		int imagenesRotadas = 0;
		for(String directorio : nombreDirectorios) {
			String fuente = pathBase + "/"+directorio;
			System.out.println("Comienza a rotar las imagenes del path: "+ directorio);
			imagenesRotadas = rotarImagenesDePath(fuente, totalImagenes, imagenesRotadas);
			System.out.println("FINALIZADO PARA EL DIRECTORIO DE IMAGENES "+ directorio);
			System.out.println("------------------------------------------------------------------------------------------------");
		}
		
		System.out.println("El sistema ha finalizado de procesar todas las imagenes");
	}
	
	private static int obtenerTotalImagenes(String base, List<String> directorios) {
		int cant = 0;
		for(String directorio : directorios) {
			Path pathDeImagenes = Paths.get(base+"/"+directorio);
			try(Stream<Path> subPaths = Files.walk(pathDeImagenes, 1)){
					List<File> imagenes = subPaths.filter(Files::isRegularFile)
							.map(Path::toFile)
							.collect(Collectors.toList());
					cant += imagenes.size();
			} catch (IOException e) {
				System.out.println("ERROR: "+e.getMessage());
				e.printStackTrace();
			}
		}
		return cant;
	}
	
	private static void mostrarProgreso(Float porcentaje) {
		int cantProgreso = porcentaje.intValue();
		String barraDeProgreso = "[----------------------------------------------------------------------------------------------------]";
		String progreso = "#";
		for(int i = 0; i<cantProgreso; i++) {
			barraDeProgreso = barraDeProgreso.replaceFirst("-", progreso);
		}
		System.out.println(barraDeProgreso);
	}
	
	private static int rotarImagenesDePath(String pathImagenes, int totalImagenes, int imagenesRotadas) {
		Path pathDeImagenes = Paths.get(pathImagenes);
		try(Stream<Path> subPaths = Files.walk(pathDeImagenes, 1)) {
			List<File> imagenes = subPaths.filter(Files::isRegularFile)
					.map(Path::toFile)
					.collect(Collectors.toList());
			int totalImagenesDeFuente = imagenes.size();
			int index = 1 + imagenesRotadas;
			Float porcentaje = (float) index/totalImagenes * 100;
			for(File imagen : imagenes) {
				porcentaje = (float) index/totalImagenes  * 100;
				System.out.println("Archivo: "+imagen.getName());
				if(imagen.getName().contains("jpg") || imagen.getName().contains("png")) {
					rotarImagenVariasVeces(imagen, pathImagenes);
					index++;
				} else {
					System.out.println("Archivo: "+imagen.getName() + " NO ES UNA IMAGEN COMPATIBLE");
				}
				System.out.println(index+" de "+ totalImagenes + " | "+porcentaje+"%");
				mostrarProgreso(porcentaje);
				imagen.deleteOnExit();
			}
			imagenesRotadas += totalImagenesDeFuente;
			System.out.println("LISTO!!!!");
		}
		catch (IOException e) {
			System.out.println("ERROR: "+e.getMessage());
			e.printStackTrace();
		}
		return imagenesRotadas;
	}
	
	public static void rotarImagenVariasVeces(File input, String basePath) throws IOException {
		String directoryEntrenamientoName = basePath.concat("/Entrenamiento");
		String directoryValidacionName = basePath.concat("/Validacion");

	    File directoryEntrenamiento = new File(directoryEntrenamientoName);
	    File directoryValidacion = new File(directoryValidacionName);
	    if (!directoryEntrenamiento.exists()){
	        directoryEntrenamiento.mkdir();
	    }
	    if (!directoryValidacion.exists()){
	    	directoryValidacion.mkdir();
	    }
		String path = input.getAbsolutePath() + "/entrenamiento";
		
		File originalOutput = new File(directoryValidacion + "/" + input.getName().substring(0, input.getName().length()-4) +".jpg");
		File outputL = new File(directoryEntrenamientoName + "/" + input.getName().substring(0, input.getName().length()-4) +"_IZQ.jpg");
		File outputD = new File(directoryEntrenamientoName + "/" + input.getName().substring(0, input.getName().length()-4) +"_DER.jpg");
		File outputT = new File(directoryEntrenamientoName + "/" + input.getName().substring(0, input.getName().length()-4) +"_ROT.jpg");
		File outputFlip = new File(directoryEntrenamientoName + "/" + input.getName().substring(0, input.getName().length()-4) +"_FLIP.jpg");
		File outputFlipRotate = new File(directoryValidacion + "/" + input.getName().substring(0, input.getName().length()-4) +"_FLIP_ROT.jpg");
		
		rotacion90Grados(input, outputL, ROTATE_LEFT);
		rotacion90Grados(input, outputD, ROTATE_RIGHT);
		rotacion180Grados(input, outputT);
		flip(input, outputFlip, FLIP_HORIZONTAL);
		flip(input, outputFlipRotate, FLIP_VERTICAL);
		guardarOriginal(input, originalOutput);
	}
	
	public static void guardarOriginal(File input, File output) throws IOException {
		
		Files.copy(input.toPath(), output.toPath());
		
	/*	
		ImageInputStream streamImage = ImageIO.createImageInputStream(input);
		Iterator<ImageReader> iterator = ImageIO.getImageReaders(streamImage);
		ImageReader reader = iterator.next();
		String format = reader.getFormatName();
		BufferedImage image = ImageIO.read(streamImage);
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage rotar = new BufferedImage(height, width, image.getType());
		ImageIO.write(rotar, format, output);
		*/
	}
	
	public static void flip(File input, File output, int direction) throws IOException {
		ImageInputStream streamImage = ImageIO.createImageInputStream(input);
		Iterator<ImageReader> iterator = ImageIO.getImageReaders(streamImage);
		ImageReader reader = iterator.next();
		String format = reader.getFormatName();
		
		BufferedImage image = ImageIO.read(streamImage);
		int width = image.getWidth();
		int height = image.getHeight();
		
		BufferedImage rotar = new BufferedImage(width, height, image.getType());
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				switch(direction) {
				case FLIP_HORIZONTAL:
					rotar.setRGB((width-1) - x, y, image.getRGB(x, y));
					break;
				case FLIP_VERTICAL:
					rotar.setRGB(x, (height-1) - y, image.getRGB(x, y));
					break;
				}	
			}
		}
		ImageIO.write(rotar, format, output);
	}
	
	public static void rotacion90Grados(File input, File output, int direction) throws IOException {
			ImageInputStream streamImage = ImageIO.createImageInputStream(input);
			Iterator<ImageReader> iterator = ImageIO.getImageReaders(streamImage);
			ImageReader reader = iterator.next();
			String format = reader.getFormatName();
			
			BufferedImage image = ImageIO.read(streamImage);
			int width = image.getWidth();
			int height = image.getHeight();
			
			BufferedImage imagen = new BufferedImage(height, width, image.getType());
			
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					switch(direction) {
					case ROTATE_LEFT:
						imagen.setRGB(y, (width-1) - x, image.getRGB(x, y));
						break;
					case ROTATE_RIGHT:
						imagen.setRGB((height-1) - y, x, image.getRGB(x, y));
					}
				}
			}
			ImageIO.write(imagen, format, output);
	}
	
	public static void rotacion180Grados(File input, File output) throws IOException {
		ImageInputStream streamImage = ImageIO.createImageInputStream(input);
		Iterator<ImageReader> iterator = ImageIO.getImageReaders(streamImage);
		ImageReader reader = iterator.next();
		String format = reader.getFormatName();
			
		BufferedImage image = ImageIO.read(streamImage);
		int width = image.getWidth();
		int height = image.getHeight();
			
		BufferedImage imagen = new BufferedImage(width, height, image.getType());
			
		for(int y = 0; y< height; y++) {
			for(int x = 0; x < width; x++) {
				imagen.setRGB((width-1) - x, (height-1) - y, image.getRGB(x, y));
			}
		}
		ImageIO.write(imagen, format, output);
	}
}

