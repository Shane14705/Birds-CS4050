package assignment.countries;

public class DataKey {
	private String countryName;
	private int countrySize;

	// default constructor
	public DataKey() {
		this(null, 0);
	}
        
	public DataKey(String name, int size) {
		countryName = name;
		countrySize = size;
	}

	public String getCountryName() {
		return countryName;
	}

	public int getCountrySize() {
		return countrySize;
	}

	/**
	 * Returns 0 if this DataKey is equal to k, returns -1 if this DataKey is smaller
	 * than k, and it returns 1 otherwise. 
	 */
	public int compareTo(DataKey k) {
            if (this.getCountrySize() == k.getCountrySize()) {
                int compare = this.countryName.compareTo(k.getCountryName());
                if (compare == 0){
                     return 0;
                } 
                else if (compare < 0) {
                    return -1;
                }
            }
            else if(this.getCountrySize() < k.getCountrySize()){
                    return -1;
            }
            return 1;
            
	}
}
