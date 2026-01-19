public class FrogPath {
    private Pond pond; // Pond object to represent the pond
    
    // Constructor to initialize the FrogPath with a given filename
    public FrogPath(String filename) {
        try {
            pond = new Pond(filename); // Initializing the pond using the provided filename
        } catch (Exception e) {
            System.out.println("Error initializing the pond: " + e.getMessage());
        }
    }
 // Method to find the best next cell for the frog to move to
    public Hexagon findBest(Hexagon currCell) {
        ArrayUniquePriorityQueue<Hexagon> canReach = new ArrayUniquePriorityQueue<Hexagon>();//A priority queue to store reachable cells
        
        for (int direction = 0; direction < 6; direction++) {
            Hexagon neighbor = currCell.getNeighbour(direction);// Get the neighbor in the current direction
            if (isValidNeighbor(neighbor, currCell, direction)) { // If the neighbor is valid and safe to move to
                double priority = calculatePriority(neighbor, direction); // Calculation of the priority of moving to the neighbor
                canReach.add(neighbor, priority);// Add the neighbor to the priority queue with its priority
            }
        }
        
        if (currCell.isLilyPadCell()) {// Check if the current cell is a lily pad cell
            for (int direction = 0; direction < 6; direction++) {
                Hexagon neighbor = currCell.getNeighbour(direction);// Neighbor in the current direction
                if (neighbor != null) {
                    for (int i = 0; i < 6; i++) {
                        Hexagon secondNeighbor = neighbor.getNeighbour(i);// Get the second neighbor in the current direction
                        
                        if (isValidSecondNeighbor(secondNeighbor, currCell, direction, i)) { 
                            double priority = calculatePriority(secondNeighbor, direction); 
                            
                            if (direction == i) {
                                priority += 0.5;//Priority increase if moving in the same direction
                            } else {
                                priority += 1.0;//Priority increase if moving in a different direction
                            }
                            
                            canReach.add(secondNeighbor, priority);// Add the second neighbor to the priority queue with its priority
                        }
                    }
                }
            }
        }
        
        if (!canReach.isEmpty()) {// If the priority queue is not empty
            try {
                return canReach.removeMin();
            } catch (CollectionException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
 // Checks if a neighbor cell is valid and safe to move to
    private boolean isValidNeighbor(Hexagon neighbor, Hexagon currCell, int direction) {
        return neighbor != null && !neighbor.isMarked() && !neighbor.isMudCell() && isSafeFromAlligators(neighbor);
    }
 // Checks if a second neighbor cell is valid and safe to move to
    private boolean isValidSecondNeighbor(Hexagon secondNeighbor, Hexagon currCell, int direction, int i) {
        return secondNeighbor != null && !secondNeighbor.isMudCell() && !secondNeighbor.isMarked() && isSafeFromAlligators(secondNeighbor) && !secondNeighbor.equals(currCell);
    }

    private double calculatePriority(Hexagon cell, int direction) {// Calculate the priority of moving to a given cell

        double priority = 0.0; // Start with a default priority of 0

        if (cell instanceof FoodHexagon) {
            int numFlies = ((FoodHexagon) cell).getNumFlies();// Get the number of flies inside of a cell

            switch (numFlies) {// Calculate priority based on the number of flies
                case 1:
                    priority = 2.0;
                    break;
                case 2:
                    priority = 1.0;
                    break;
                default:
                    priority = 0.0;
            }
        } else if (cell.isEnd()) {
            priority = 3.0;
        } else if (cell.isLilyPadCell()) {
            priority = 4.0;
        } else if (cell.isReedsCell()) {
            boolean nearAlligator = false;
            for (int i = 0; i < 6; i++) {  // Check if the cell is adjacent to an alligator
                try {
                    Hexagon neighbour = cell.getNeighbour(i);
                    if (neighbour != null && neighbour.isAlligator()) {
                        nearAlligator = true;
                        break;
                    }
                } catch (InvalidNeighbourIndexException e) {
                    // Handle exception
                }
            }
            priority = nearAlligator ? 10.0 : 5.0;
        } else if (cell.isWaterCell()) {
            priority = 6.0;
        }

        return priority;
    }

    private boolean isSafeFromAlligators(Hexagon cell) {// Method to check if a hexagon is safe from alligators
        return !cell.isAlligator() && !isAdjacentToAlligator(cell);
    }

    private boolean isAdjacentToAlligator(Hexagon cell) {// Method to check if a hexagon is adjacent to an alligator
        for (int i = 0; i < 6; i++) {
            try {
                Hexagon neighbour = cell.getNeighbour(i);// Get the neighbor of the current cell
                if (neighbour != null && neighbour.isAlligator() && !cell.isReedsCell()) {
                    return true;
                }
            } catch (InvalidNeighbourIndexException ignored) {
            	
            }
        }
        return false;
    }
    
    public String findPath() {// Method to find the frog's path through the pond
        ArrayStack<Hexagon> S = new ArrayStack<>(); // Create a stack to track the frog's path
        Hexagon startCell = pond.getStart(); // Get the starting cell of the pond
        S.push(startCell); // Push the starting cell onto the stack
        startCell.markInStack(); // Marking the starting cell as visited
        int fliesEaten = 0; //Flies eaten by the frog
        String pathString = ""; // Store the frog's path

        while (!S.isEmpty()) {
            Hexagon curr = S.peek(); // Get the current cell from the top of the stack
            pathString += curr.getID() + " "; //ID of the current cell to the path string
            if (curr.isEnd()) {
                break;
            }
            // Check if the current cell contains flies 
            if (curr instanceof FoodHexagon) {
                FoodHexagon foodCell = (FoodHexagon) curr;
                fliesEaten += foodCell.getNumFlies(); // Increment the counter by the number of flies in the cell
                foodCell.removeFlies(); 
            }
            
            Hexagon next = findBest(curr); // Find the best next cell for the frog to move to
            if (next == null) {
                curr = S.pop(); // Pop the current cell from the stack if no valid next cell is found
                curr.markOutStack(); // Mark the current cell 
            } else {
                S.push(next); // Push the next cell onto the stack
                next.markInStack(); // Mark the next cell 
            }
        }
        if (S.isEmpty()) {
            return "No solution"; 
        } else {
            pathString += "ate " + fliesEaten + " flies"; //The number of flies eaten by the frog to the path string
            return pathString; 
        }
    }
    
    public static void main(String[] args) {
        // If correct number of arguments is provided
        if (args.length != 1) {
            System.out.println("Usage: java FrogPath <pondFilename>");
        } else {
            FrogPath frogPath = new FrogPath(args[0]); // FrogPath object created with the specified pond filename
            System.out.println(frogPath.findPath()); 
        }
    }
}