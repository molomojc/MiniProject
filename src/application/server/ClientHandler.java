package application.server;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
//import java.awt.image.RescaleOp;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

/**
 * Handles client requests and processes image-related operations.
 * This class implements the Runnable interface to handle client connections in a multithreaded server.
 * 
 * Responsibilities include:
 * - Uploading and downloading images.
 * - Image processing (e.g., grayscale conversion, blurring, edge detection).
 * - K-Nearest Neighbors (KNN) classification.
 * - Graph creation and similarity calculation.
 **/
public class ClientHandler implements Runnable {
    //constant
      	private final Socket sClient;
	    private BufferedReader lnReader;
	    private PrintWriter lWriter;
	    private DataInputStream ByteRead; //reading different types of data to
	    private DataOutputStream writeByte;  //Writing different types of data
	    /*private Patch patch;
	    private Node node;*/
	
	    
	    public ClientHandler(Socket socketClient) {
	        this.sClient = socketClient;
	        try {
	            lnReader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
	            lWriter = new PrintWriter(socketClient.getOutputStream(), true);
	            ByteRead = new DataInputStream(socketClient.getInputStream());
	            writeByte = new DataOutputStream(socketClient.getOutputStream());
	            uploadInfectedReferenc();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	@Override
	public void run() {
		try {
			String line[] = lnReader.readLine().split(" "); //Store the Command
			if(line[0].equals("LIST")) {
				//call the method to pull the imgList
				System.out.println("a request for list was made..");
				pull();
			}else if(line[0].equals("DOWN")) {
				System.out.println("a request for download was made..");
				download(line[1]);
			}
			else if(line[0].equals("UP")) {
				System.out.println("a request for UPLOADING was made..");
			     
				String name = line[1]; //get the name of the image
				//save  uploaded image 
                upload(name);
                
                System.out.println("Upload complete");
                //process the image 
                try {
                	 System.out.println("Converting to greyscale");
                     //  convert the picture to greyscale
                       convertToGreyscale("data/server/" + name + ".png");
                       BufferedImage img = ImageIO.read(new File("data/server/greyscale_" + name + ".png"));

                    // Add this resize (Fix #3) - right after loading the image
                    if (img.getWidth() > 500 || img.getHeight() > 500) {
                        img = resizeImage(img, 500, 500);
                        System.out.println("Resized image for testing");
                    }
                       System.out.println("Converting to greyscale complete");
                       //Write to a text file
                       WritePixelsToTxt(img, line[1]);
                    //blur out the image
       				System.out.println("Blurring the image");
       				
       				BufferedImage blurImg = heavyblur(heavyblur(img)); //double blur it
       				ImageIO.write(blurImg, "png", new File("data/server/blurred_" + name + ".png"));
       				System.out.println("Blurring complete");
       				
       				//edge detection
       				System.out.println("Detecting edges using sobel operator");
       				BufferedImage edgeImg = detectEdges(img);
       				ImageIO.write(edgeImg, "png", new File("data/server/edge_" + line[1] + ".png"));
       				System.out.println("Edge detection complete");
       				
       				//begining the KNN
       				System.out.println("Creating patches");
       				List<Patch> patches = createPatches(img, edgeImg);
       				System.out.println("Patches created");  
       				//call the KNN
       				System.out.println("Classifying the image");
       				if (patches == null || patches.isEmpty()) {
       				    System.err.println("ERROR: No patches were created");
       				    lWriter.println("ERROR: No patches created");
       				    return;
       				}
       				BufferedImage knnImg = KNN(blurImg, patches, 3);
       				//create the image
       				ImageIO.write(knnImg, "png", new File("data/server/knn_" + line[1] + ".png"));
       				System.out.println("KNN complete");
       				
       				List<Point> points = groupNodes(knnImg);
       				//Points with nodes
       				List<Point> nodes = gatherNodes(points, knnImg);
       				//A map which contains the nodes
       				Map<Point, Node> graphNodes = createGraph(nodes, knnImg);
       				
       				

                	System.out.println("Calculating similarity ");
                	//checks if data is loaded 

                		
                		double sim = CalculateSimilarity(knnImg);
                		System.out.println("Similarity percent:" + sim +"%");
//                		lWriter.println("Similarity percent:" + sim +"%");
                	//	lWriter.println("Diagnosis results :" + sim);
                		
              
                	WriteInfo(line[1], line[2], line[3], line[4], sim);
                }catch(IOException e) {
                	lWriter.println("ERROR: FAILED TO PROCESS IMAGE");
                	System.err.println("Image processing error:" +e.getMessage());
                	e.printStackTrace();
                }
                lWriter.flush();
               
                
               
            }
			else {
                System.err.println("Invalid request from user");
              
            }
			lnReader.close(); //close the BufferReader
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			try {
				sClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	 public void download(String ID) {
	        try (BufferedReader reader = new BufferedReader(new FileReader(new File("data/server/ImgList.txt")))) {
	            String line;

	            while ((line = reader.readLine()) != null) {
	                if (line.contains(ID)) {
	                    String tempArray[] = line.split(" ");
	                    String fileName = tempArray[1];

	                    // Sends the file name to the client
	                    lWriter.println(fileName);
                        lWriter.flush();

	                    File imageFile = new File("data/server/" + fileName); // Updates the path
	                    FileInputStream imageFis = new FileInputStream(imageFile);
	                    byte[] buffer = new byte[1024];
	                    int bytesRead;

	                    while ((bytesRead = imageFis.read(buffer)) != -1) {
	                        writeByte.write(buffer, 0, bytesRead);
	                        writeByte.flush();
	                    }
	                    imageFis.close();
	                    break;
	                }
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	 
	 //TODO: Convert the image to greyscale done
	 //TODO: Do blurs done
	 //TODO: Do edge detection done
	 //TODO: Formulate the Nodes
	 //TODO: Send the Nodes to the client
	 //TODO: client draw graph based on the nodes
     
	 /**
	 *this method fetches the image in /data/server and converts it to greyscale
	 * @throws IOException 
	 *
	 */
	 public void convertToGreyscale(String path) throws IOException {
		  File[] imageFiles = new File("data/server/").listFiles((dir, name) -> name.endsWith(".png"));
		  
		    System.out.println("Found the target image files");
	        if (imageFiles == null) {
	            System.out.println("No image files found in the directory.");
	            return;
	            
	        }
	        File file = new File(path);
	        
	      
           //Handle a known image
	    //    for (File file : imageFiles) {
	            BufferedImage img = ImageIO.read(file);
	           System.out.println("Convert to greyscale");
	           System.out.println("Image name: " + file.getAbsolutePath());
	           
	           //Create a new Buffered with same Dimensions
	           BufferedImage greyImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
	           //initiate the Graphics
	           int rgb=0, r=0, g=0, b=0;
	           //because we have rows and cols
	           //This is a general way of doing this
	           for(int row=0; row<img.getHeight(); row++) {
	        	   for(int col=0; col<img.getWidth(); col++) {
	        		   //get the pixel
	        		   rgb = img.getRGB(col, row);
	        		   //get the color values shifting the bits
	        		   r = (rgb >> 16) & 0xFF;
	        		   g = (rgb >> 8) & 0xFF;
	        		   b = (rgb) & 0xFF;
	        		   //calculate the average
	        		   int avg = (r + g + b) / 3;
	        		   //set the pixel to the new image
	        		   greyImg.setRGB(col, row, (avg << 16) | (avg << 8) | avg);
	        	   }
	           }
	           System.out.println("Image converted to greyscale");
	           //save the image
	           File outputFile = new File("data/server/greyscale_" + file.getName());
	           //write the image to the file
	           ImageIO.write(greyImg, "png", outputFile);
	  //          }
	        }
	 

	 public void upload(String Name) {
		    try {
		        // Create a file output stream to write the image to the server directory
		        File outputFile = new File("data/server/" + Name + ".png");
		        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));
		        
		        // Append image information to a list file
		       PrintWriter fileWriter = new PrintWriter(new FileOutputStream("data/server/ImgList.txt", true));
		        fileWriter.write("\n"+ Name + ".png"); //Jacob.png
		        fileWriter.flush();
		        fileWriter.close();

		        // Buffer for reading data
		        byte[] byteArray = new byte[1024];
		        int bytesRead;

		        // Read from the input stream and write to the file output stream
		        while ((bytesRead = ByteRead.read(byteArray)) != -1) {
		            bos.write(byteArray, 0, bytesRead);
		        }
		        
		        bos.flush();
		        bos.close(); // Close the output stream once writing is done

		        System.out.println("Upload successful: " + outputFile.getAbsolutePath());

		    } catch (FileNotFoundException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}

    /**
     * Sends a list of files to the client.
     */
    public void pull() {
        try (BufferedReader readFile = new BufferedReader(new InputStreamReader(new FileInputStream(new File("data/server/ImgList.txt"))))) {
            String line;
            while ((line = readFile.readLine()) != null) {
            	lWriter.write(line + "\n");
            	lWriter.flush();
            }
            readFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Blurs the image
     * 
     */
    public  BufferedImage heavyblur (BufferedImage img) {
		BufferedImage blurImg = new BufferedImage(
			img.getWidth()-4, img.getHeight()-4, BufferedImage.TYPE_BYTE_GRAY);
		int pix = 0;
		for (int y=0; y<blurImg.getHeight(); y++) {
			for (int x=0; x<blurImg.getWidth(); x++) {
				pix = (int)(
				10*(img.getRGB(x+3, y+3)& 0xFF)
				+ 6*(img.getRGB(x+2, y+1)& 0xFF)
				+ 6*(img.getRGB(x+1, y+2)& 0xFF)
				+ 6*(img.getRGB(x+2, y+3)& 0xFF)
				+ 6*(img.getRGB(x+3, y+2)& 0xFF)
				+ 4*(img.getRGB(x+1, y+1)& 0xFF)
				+ 4*(img.getRGB(x+1, y+3)& 0xFF)
				+ 4*(img.getRGB(x+3, y+1)& 0xFF)
				+ 4*(img.getRGB(x+3, y+3)& 0xFF)
				+ 2*(img.getRGB(x, y+1)& 0xFF)
				+ 2*(img.getRGB(x, y+2)& 0xFF)
				+ 2*(img.getRGB(x, y+3)& 0xFF)
				+ 2*(img.getRGB(x+4, y+1)& 0xFF)
				+ 2*(img.getRGB(x+4, y+2)& 0xFF)
				+ 2*(img.getRGB(x+4, y+3)& 0xFF)
				+ 2*(img.getRGB(x+1, y)& 0xFF)
				+ 2*(img.getRGB(x+2, y)& 0xFF)
				+ 2*(img.getRGB(x+3, y)& 0xFF)
				+ 2*(img.getRGB(x+1, y+4)& 0xFF)
				+ 2*(img.getRGB(x+2, y+4)& 0xFF)
				+ 2*(img.getRGB(x+3, y+4)& 0xFF)
				+ (img.getRGB(x, y)& 0xFF)
				+ (img.getRGB(x, y+2)& 0xFF)
				+ (img.getRGB(x+2, y)& 0xFF)
				+ (img.getRGB(x+2, y+2)& 0xFF))/74;
				int p = (255<<24) | (pix<<16) | (pix<<8) | pix; 
				blurImg.setRGB(x,y,p);
			}
		}
		return blurImg;
	}

    
    /**
	 * Detects edges in the image using Sobel operator
	 * 
	 */
    public static BufferedImage detectEdges (BufferedImage img) {
		int h = img.getHeight(), w = img.getWidth(), threshold=30, p = 0;
		BufferedImage edgeImg = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
		int[][] vert = new int[w][h];
		int[][] horiz = new int[w][h];
		int[][] edgeWeight = new int[w][h];
		for (int y=1; y<h-1; y++) {
			for (int x=1; x<w-1; x++) {
				vert[x][y] = (int)(img.getRGB(x+1, y-1)& 0xFF + 2*(img.getRGB(x+1, y)& 0xFF) + img.getRGB(x+1, y+1)& 0xFF
					- img.getRGB(x-1, y-1)& 0xFF - 2*(img.getRGB(x-1, y)& 0xFF) - img.getRGB(x-1, y+1)& 0xFF);
				horiz[x][y] = (int)(img.getRGB(x-1, y+1)& 0xFF + 2*(img.getRGB(x, y+1)& 0xFF) + img.getRGB(x+1, y+1)& 0xFF
					- img.getRGB(x-1, y-1)& 0xFF - 2*(img.getRGB(x, y-1)& 0xFF) - img.getRGB(x+1, y-1)& 0xFF);
				edgeWeight[x][y] = (int)(Math.sqrt(vert[x][y] * vert[x][y] + horiz[x][y] * horiz[x][y]));
				if (edgeWeight[x][y] > threshold)
					p = (255<<24) | (255<<16) | (255<<8) | 255;
				else 
					p = (255<<24) | (0<<16) | (0<<8) | 0; 
				edgeImg.setRGB(x,y,p);
			}
		}
		return edgeImg;
	}
    //create a list of these patches
    public List<Patch> createPatches(BufferedImage greyImg, BufferedImage edgeImg) {
        List<Patch> patches = new ArrayList<>();
        int stride = 2; // Sample every other pixel to reduce computation
        
        for (int y = 1; y < greyImg.getHeight() - 1; y += stride) {
            for (int x = 1; x < greyImg.getWidth() - 1; x += stride) {
                int[] featureVector = extractPatchFeatures(greyImg, x, y);
                int edgePixel = edgeImg.getRGB(x, y) & 0xFF;
                patches.add(new Patch(featureVector, edgePixel > 128 ? 1 : 0));
            }
        }
        return patches;
    }
    
    //calculate the euclidean distance between two patches
    /**
     * 
     * This returns the distance between two points
     *  
     *     (x2-x1)
     *  
     * @param p1
     * @param p2
     * @return
     */
    private double Distance(int[] a, int[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
    
    /**
	 * 
	 * This will classify the patch
	 * for kNN we use Priority Queue to group the most similar n
	 * @param p1
	 * @param p2
	 * @return
	 */
    //This basically the general patch for that certain pixel you get
    public int classify(int[] matrix, List<Patch> patches, int k) {
        // Use a min-heap to keep track of k nearest neighbors
        PriorityQueue<PatchDistance> nearestNeighbors = new PriorityQueue<>(
            k, Comparator.comparingDouble(PatchDistance::getDistance).reversed());
        
        for (Patch patch : patches) {
            double currentDistance = Distance(matrix, patch.featureVector);
            
            if (nearestNeighbors.size() < k) {
                nearestNeighbors.add(new PatchDistance(patch, currentDistance));
            } else if (currentDistance < nearestNeighbors.peek().distance) {
                nearestNeighbors.poll();
                nearestNeighbors.add(new PatchDistance(patch, currentDistance));
            }
        }
        
        int edgeCount = 0;
        for (PatchDistance pd : nearestNeighbors) {
            if (pd.patch.classify == 1) {
                edgeCount++;
            }
        }
        
        return edgeCount > (k / 2) ? 1 : 0;
    }

    private static class PatchDistance {
        final Patch patch;
        final double distance;
        
        PatchDistance(Patch patch, double distance) {
            this.patch = patch;
            this.distance = distance;
        }
        
        double getDistance() {
            return distance;
        }
    }
    
    /**
     * The KNN algorithm
     */
    public BufferedImage KNN(BufferedImage img, List<Patch> patches, int k) {
        System.out.println("Starting KNN classification...");
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage knnImg = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        
        for(int y = 1 ; y < height - 1; y++) {
            // Simple progress indicator
            if(y % 50 == 0) System.out.println("Processing row: " + y + "/" + height);
            
            for(int x = 1; x < width - 1; x++) {
                int[] featureVector = new int[9];
                int index = 0;
                
                for(int row = -1; row <= 1; row++) {
                    for(int col = -1; col <= 1; col++) {
                        int pixel = img.getRGB(x + col, y + row) & 0xFF;
                        featureVector[index++] = pixel;
                    }
                }
                
                int classify = classify(featureVector, patches, k);
                int pixel = (classify == 1) ? 255 : 0;
                int p = (255<<24) | (pixel<<16) | (pixel<<8) | pixel;
                knnImg.setRGB(x, y, p);
            }
        }
        
        return knnImg;
    }

    private int[] extractPatchFeatures(BufferedImage img, int centerX, int centerY) {
        int[] features = new int[9];
        int index = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                features[index++] = img.getRGB(centerX + dx, centerY + dy) & 0xFF;
            }
        }
        return features;
    }
    //Grouping the Nodes into a list of points
    public List<Point> groupNodes(BufferedImage img) {
		List<Point> points = new ArrayList<>();
		int width = img.getWidth();
		int height = img.getHeight();
		//loop through the image
		for(int y = 0; y < height - 1; y++) {
			for(int x = 0; x < width -1 ; x++) {
				int pixel = img.getRGB(x, y) & 0xFF;
				//we want edges
				if(pixel >0)
				{
					//add the (x,y) coordinates to the list
					points.add(new Point(x, y));
				}
			}
		}
		return points;
	}
    
    //lets create a node
    //So a node is a point that has only one neighbor
    //so lets check each point and see if it has a neighbor
    
    boolean hasNeighbours(int x, int y, BufferedImage img) {
    	boolean hasNeighbours = false;
    	int CountNeighbours = 0; //if it has more than 1 neighbour has neighbours = false
    	for(int row = -1; row <= 1; row++) {
			for(int col = -1; col <= 1; col++) {
				//get the pixel
				int pixel = img.getRGB(x + col, y + row) & 0xFF;
				if(pixel > 0) {
					CountNeighbours++;
				}
			}
			if(CountNeighbours > 1) {
				hasNeighbours = false; //it has more than one neighbour
			}
			else {
				hasNeighbours = true; //it has only one neighbour
			}
		}
    	
    	return hasNeighbours;
    }
    
    //Now gather the Nodes
    public List<Point> gatherNodes(List<Point> Points, BufferedImage img) {
		List<Point> nodes = new ArrayList<>();
		//loop through the points
		for(Point point : Points) {
			int x = point.x;
			int y = point.y;
			//check if the point has neighbours
			if(hasNeighbours(x, y, img)) {
				nodes.add(point); //add the node to the list
			}
		}
	
	//Now draw the edges between the nodes
	
		return nodes;
    }
    //Draw the edges between the nodes
    public void traceToConnectedNodes(Node node, BufferedImage edgeImage, Map<Point, Node> graphNodes) {
        int width = edgeImage.getWidth();
        int height = edgeImage.getHeight();

        // 8 directions
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < 8; i++) {
            int newX = node.x + dx[i];
            int newY = node.y + dy[i];

            // Check bounds
            if (newX < 0 || newY < 0 || newX >= width || newY >= height)
                continue;

            int pixel = edgeImage.getRGB(newX, newY) & 0xFF;
            if (pixel > 128) { // It's a white pixel (part of an edge)
                Point point = new Point(newX, newY);
                Node neighbor = graphNodes.get(point);
                if (neighbor == null) {
                    neighbor = new Node(newX, newY);
                    graphNodes.put(point, neighbor);
                    // Recursively explore
                    traceToConnectedNodes(neighbor, edgeImage, graphNodes);
                }

                // Link the nodes if not already linked
                if (!node.neighbors.contains(neighbor))
                    node.neighbors.add(neighbor);
                if (!neighbor.neighbors.contains(node))
                    neighbor.neighbors.add(node);
            }
        }
    }

   //Create a map of nodes
	public Map<Point, Node> createGraph(List<Point> points, BufferedImage edgeImage) {
		
		Map<Point, Node> graphNodes = new HashMap<>();

		for (int y = 0; y < edgeImage.getHeight(); y++) {
		    for (int x = 0; x < edgeImage.getWidth(); x++) {
		        int pixel = edgeImage.getRGB(x, y) & 0xFF;
		        if (pixel > 128) {
		            Point pt = new Point(x, y);
		            if (!graphNodes.containsKey(pt)) {
		                Node node = new Node(x, y);
		                graphNodes.put(pt, node);
		                traceToConnectedNodes(node, edgeImage, graphNodes);
		            }
		        }
		    }
		}


		return graphNodes;
	} 

	public BufferedImage drawGraph(
		    BufferedImage baseImage,
		    Set<Point> graphNodes,
		    Map<Point, List<Point>> graphEdges
		) {
		    // Make a copy of the image to draw on
		    BufferedImage finalImage = new BufferedImage(
		        baseImage.getWidth(),
		        baseImage.getHeight(),
		        BufferedImage.TYPE_INT_ARGB
		    );
		    Graphics2D g = finalImage.createGraphics();

		    // Draw the base image first
		    g.drawImage(baseImage, 0, 0, null);

		    // Draw nodes in RED
		    g.setColor(Color.RED);
		    for (Point node : graphNodes) {
		        g.fillOval(node.x - 2, node.y - 2, 5, 5); // Small red dot at node
		    }

		    // Draw edges in BLUE
		    g.setColor(Color.BLUE);
		    for (Map.Entry<Point, List<Point>> entry : graphEdges.entrySet()) {
		        Point from = entry.getKey();
		        for (Point to : entry.getValue()) {
		            g.drawLine(from.x, from.y, to.x, to.y);
		        }
		    }

		    g.dispose();
		    return finalImage;
		}

	public Map<Point, List<Point>> convertToEdgeMap(Map<Point, Node> graphNodes) {
	    Map<Point, List<Point>> graph = new HashMap<>();
	    for (Map.Entry<Point, Node> entry : graphNodes.entrySet()) {
	        Point key = entry.getKey();
	        Node node = entry.getValue();
	        List<Point> neighbors = node.neighbors.stream()
	            .map(n -> new Point(n.x, n.y))
	            .collect(Collectors.toList());
	        graph.put(key, neighbors);
	    }
	    return graph;
	}
	
	private void uploadInfectedReferenc() throws IOException {
		convertToGreyscale("src/assets/xray.png");
		System.out.println("Converting to greyscale");
		BufferedImage ImageInfec = ImageIO.read(new File("data/server/greyscale_"+"xray.png"));
		ImageIO.write(ImageInfec, "png", new File("data/server/greyscale_xray.png"));
		System.out.println("Converting to greyscale complete");
		BufferedImage Bimage=heavyblur(heavyblur(ImageInfec));
		ImageIO.write(Bimage, "png", new File("data/server/heavyblurred_xray.png"));
		BufferedImage detectedImg = detectEdges(Bimage);
		ImageIO.write(detectedImg,"png", new File("data/server/edge_xray.png"));
		List<Patch> ImageP=createPatches(Bimage, detectedImg);
		BufferedImage patchkNN=KNN(Bimage, ImageP, 3);
		ImageIO.write(patchkNN,"png", new File("data/server/knn_xray.png"));
		WritePixelsToTxt(patchkNN, "PatchKnn_xray");
	}
	
	public double CalculateSimilarity(BufferedImage ClientImage) throws IOException {
		
		//Preprocess the client image
//		ClientImage = preprocessImg(ClientImage);
		
		//load infected pixel values
		File InfectedpixelsFile = new File("data/server/PatchKnn_xray_pixels.txt");
		if(!InfectedpixelsFile.exists()) {
			System.err.println("Infected pixels not found");
			return 0;
		}
		
		Map<Point, Integer> infectedPixels = new HashMap<>();
	    try (BufferedReader reader = new BufferedReader(new FileReader(InfectedpixelsFile))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            // Expected format: (x,y):value
	            int open = line.indexOf('(');
	            int comma = line.indexOf(',', open);
	            int close = line.indexOf(')', comma);
	            int colon = line.indexOf(':', close);

	            int x = Integer.parseInt(line.substring(open + 1, comma));
	            int y = Integer.parseInt(line.substring(comma + 1, close));
	            int value = Integer.parseInt(line.substring(colon + 1));

	            infectedPixels.put(new Point(x, y), value);
	        }
	    } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    BufferedImage ImageOutput = new BufferedImage(ClientImage.getWidth(), ClientImage.getHeight(), BufferedImage.TYPE_INT_RGB);
	    Graphics2D graphics = ImageOutput.createGraphics();
	    graphics.drawImage(ClientImage, 0, 0, null);
		 
		int pixelsMatched  = 0;
		int PixelsTotal = 0;
		
		for(Map.Entry<Point , Integer> entries : infectedPixels.entrySet()) {
			
			Point point = entries.getKey();
			double referenceValue = entries.getValue();
			
			if(point.x < ClientImage.getWidth() && point.y < ClientImage.getHeight()) {
				int ClientValue = ClientImage.getRGB(point.x, point.y) &0xFF;
				
				if(Math.abs(ClientValue - referenceValue) <= 0.1 * referenceValue) {
					
					System.out.println("Patch Matched: " + point);
					ImageOutput.setRGB(point.x, point.y,new Color(0,255,0).getRGB());
					pixelsMatched++;
				}PixelsTotal++;
			}
		}
		
		graphics.dispose();
		
		//save outputimage
		
		File outputImage = new File("data/server/PixelsMatched.png");
		ImageIO.write(ImageOutput, "png", outputImage);
		
		
		System.out.println("Pixels Matched: " + pixelsMatched);
		return PixelsTotal == 0 ? 0 :(double) pixelsMatched/ PixelsTotal*100;
		
	}
	
	public void WritePixelsToTxt(BufferedImage image, String filename) throws IOException {
	    File txtFile = new File("data/server/" + filename + "_pixels.txt");
	    try (PrintWriter writer = new PrintWriter(txtFile)) {
	        for (int y = 0; y < image.getHeight(); y++) {
	            for (int x = 0; x < image.getWidth(); x++) {
	                int pixel = image.getRGB(x, y) & 0xFF; // Get grayscale value
	                writer.printf("(%d,%d):%d\n", x, y, pixel); // Format: (x,y):pixelValue
	            }
	        }
	    }
	}
	private BufferedImage resizeImage(BufferedImage original, int targetWidth, int targetHeight) {
	    BufferedImage resized = new BufferedImage(targetWidth, targetHeight, original.getType());
	    Graphics2D g = resized.createGraphics();
	    g.drawImage(original, 0, 0, targetWidth, targetHeight, null);
	    g.dispose();
	    return resized;
	}
	
	public void WriteInfo(String name,String age,String symptoms,String infected,double Similarity) throws FileNotFoundException {
		//Add TRUE for automatic append
			PrintWriter Infotxt = new PrintWriter(new FileOutputStream("data/server/Info.txt"));
	        Infotxt.write(name +" " + age+" " + symptoms+" "+infected+" "+ Similarity+"\n" ); 
	        Infotxt.flush();
	        Infotxt.close();
	}
}
