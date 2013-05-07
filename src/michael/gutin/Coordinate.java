package michael.gutin;

public class Coordinate {
	
	  		public int x;
	        public int y;

	        public Coordinate(int x, int y) {
	            this.x = x;
	            this.y = y;
	        }

	        public boolean equals(Coordinate other) {
	        	if(other==null) return false;
	            if (x == other.x && y == other.y) 	return true;
	            return false;
	        }
	        
	        public Coordinate copy()
	        {
	        	return new Coordinate(this.x,this.y);
	        }

	        @Override
	        public String toString() {
	            return "(" + x + ", " + y + ")";
	        }
	    }
