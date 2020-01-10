public class Seam {
    private double energy;
    private int[] pixels;
    private String direction;
    private int size;

    public Seam(int s, String dir)
    {
        this.size = s;
		this.pixels = new int[s];
        this.direction = dir;
    }

    // return  "horizontal" || "vertical"
    String getDirection()
    {
        return direction;
    }

    // return pixels of the path
    int[] getPixels()
    {
        return pixels;
    }
    
    // get seam energy
    double getEnergy()
    {
        return energy;
    }

    // get the size of the seam
    int getSize()
    {
        return size;
    }
    
    // set pixels    
    void setPixels(int position, int value)
    {
        pixels[position] = value;
    }
    
    // set seam energy
    void setEnergy(double energy)
    {
        this.energy = energy;
    }
}
