import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {
    private Picture pic;
    private double [][] energy;
    private int width;
    private int height;

    public SeamCarver(Picture picture)
    {
        if (picture == null) {
            throw new IllegalArgumentException("Picture is null.");
        }
        pic=picture;
        width = pic.width();
        height = pic.height();
        energy =new double [width][height];
        for(int i=0;i<width;i++) {
            energy[i][0]=energy[i][height-1]=1000;
        }
        for(int i = 1;i<height-1;i++)
        {
            energy[0][i]=energy[width-1][i]=1000;
        }
        for(int i=1;i<height-1;i++)
            for(int j=1;j<width-1;j++)
                calcEnergy(j,i);
    }
    public Picture picture() {
        Picture picture = new Picture(width, height);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                picture.set(x, y, pic.get(x, y));
            }
        }
        return picture;
    }
    private void calcEnergy(int j, int i)
    {
        if (j < 0 || j >= width || i < 0 || i >= height) {
            throw new IllegalArgumentException("Indices are out of boundary.");
        }
        Color color1 = pic.get(j-1, i);
        Color color2 = pic.get(j+1, i);
        int red = color1.getRed() - color2.getRed();
        int green = color1.getGreen() - color2.getGreen();
        int blue = color1.getBlue() - color2.getBlue();
        energy[j][i]= red * red + green * green + blue * blue;
        color1 = pic.get(j, i+1);
        color2 = pic.get(j, i-1);
        red = color1.getRed() - color2.getRed();
        green = color1.getGreen() - color2.getGreen();
        blue = color1.getBlue() - color2.getBlue();
        energy[j][i]+= red * red + green * green + blue * blue;
        energy[j][i] = Math.sqrt(energy[j][i]);
    }
    public int width()
    {
        return width;
    }
    public int height()
    {
        return height;
    }
    public double energy(int x, int y)
    {if (x < 0 || x >= width || y < 0 || y >= height) {
        throw new IllegalArgumentException("Indices are out of boundary.");
    }
        return energy[x][y];
    }
    private int min(double[] mass, int j1, int j2, int j3)
    {
        if(mass[j1]<=mass[j2])
            if(mass[j1]<=mass[j3])
                return j1;
            else return j3;
        else if (mass[j2]<=mass[j3])
            return j2;
        else return j3;

    }
    private int min1(int i, int j1, int j2, int j3)
    {
        if(energy[i][j1]<=energy[i][j2])
            if(energy[i][j1]<=energy[i][j3])
                return j1;
            else return j3;
        else if (energy[i][j2]<=energy[i][j3])
            return j2;
        else return j3;

    }
    public int[] findVerticalSeam()
    {
        double [][] weights = new double[2][width];
        for(int i=0;i<width;i++) {
            weights[0][i] = 1000;
            weights[1][i] = 1000;
        }
        weights[0][0]*=height;
        weights[0][width-1]*=height;
        weights[1][0]*=height;
        weights[1][width-1]*=height;
        int [][] edgeTo = new int[width][height];
        for(int i=1;i<height;i++)
            for(int j=1;j<width-1;j++)
            {
                edgeTo[j][i]=min(weights[i%2],j-1,j,j+1);
                weights[(i+1)%2][j]=weights[i%2][edgeTo[j][i]]+energy[j][i];
            }
        double min =Double.POSITIVE_INFINITY;
        int j=0;
        for(int i=1;i<width-1;i++)
            if(weights[height%2][i]<min) {
                min = weights[height%2][i];
                j=i;
            }
        int[]ans=new int[height];
        ans[height-1]=j;
        for (int i = 1;i<height;i++)
            ans[height-1-i]=edgeTo[ans[height-i]][height - i];
        return ans;

    }
    public int[] findHorizontalSeam()
    {
        double [][] weights = new double[2][height];
        for(int i=0;i<height;i++) {
            weights[0][i] = 1000;
            weights[1][i] = 1000;
        }
        weights[0][0]*=width;
        weights[0][height-1]*=width;
        weights[1][0]*=width;
        weights[1][height-1]*=width;
        int [][] edgeTo = new int[width][height];
        for(int i=1;i<width;i++)
            for(int j=1;j<height-1;j++)
            {
                edgeTo[i][j]=min(weights[i%2],j-1,j,j+1);
                weights[(i+1)%2][j]=weights[i%2][edgeTo[i][j]]+energy[i][j];
            }
        double min =Double.POSITIVE_INFINITY;
        int j=0;
        for(int i=1;i<height-1;i++)
            if(weights[width%2][i]<min) {
                min = weights[width%2][i];
                j=i;
            }
        int[]ans=new int[width];
        ans[width-1]=j;
        for (int i = 1;i<width;i++)
            ans[width-1-i]=edgeTo[width-i][ans[width-i]];
        return ans;

    }
    public void removeVerticalSeam(int[] seam)
    {
        if (seam == null || seam.length != height) {
            throw new IllegalArgumentException("Seam is illegal.");
        }

        if (width <= 1) {
            throw new IllegalArgumentException("Width of the picture is less than or equal to 1");
        }

        for(int i=0;i<height;i++){
            if (seam[i] < 0 || seam[i] >= width || (i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1)) {
                throw new IllegalArgumentException("Seam is illegal.");
            }
            for(int j=seam[i];j<width-1;j++)
            {
                pic.set(j,i,pic.get(j+1,i));
                energy[j][i] = energy[j+1][i];
            }}
        width--;
        for(int i=1;i<height-1;i++)
        {
            if(energy(seam[i],i)!=1000)
                calcEnergy(seam[i],i);
            if(energy(seam[i]-1,i)!=1000)
                calcEnergy(seam[i]-1,i);
        }


    }

    public void removeHorizontalSeam(int[] seam) {
        if (seam == null || seam.length != width) {
            throw new IllegalArgumentException("Seam is illegal.");
        }
        if (height <= 1) {
            throw new IllegalArgumentException("Height of the picture is less than or equal to 1");
        }
        for (int i = 0; i < width; i++){
            if (seam[i] < 0 || seam[i] >= height || (i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1)) {
                throw new IllegalArgumentException("Seam is illegal.");
            }
            for (int j = seam[i]; j < height - 1; j++) {
                pic.set(i, j, pic.get(i, j + 1));
                energy[i][j] = energy[i][j + 1];
            }}
        height--;
        for (int i = 1; i < width - 1; i++) {
            if (energy(i, seam[i]) != 1000)
                calcEnergy(i, seam[i]);
            if (energy(i, seam[i] - 1) != 1000)
                calcEnergy(i, seam[i] - 1);
        }
    }
}


