# SeamCarving

It functions by establishing a number of seams (paths of least importance) in an image and removes seams to reduce image size or inserts seams to extend it.

Detail information: <a href="https://en.wikipedia.org/wiki/Seam_carving">WiKi</a>

Language: JAVA 

Owner: CYT

----------------------------------------------------------

## **Step1.** 
Import the picture by clicking the `choose` button, and you will see a picture below and the size of it in the textfield.

<img src="https://github.com/CYT823/SeamCarving/blob/master/git_images/sc1.png" width="600"/>

## **Step2.** 
You can reduce or extend the picture by pressing `↑` `↓` `←` `→`. This work will start to calculate which seam has the lowest energy, and then remove or insert it.

<img src="https://github.com/CYT823/SeamCarving/blob/master/git_images/sc2.png" width="600"/>

As you can see, the left one is the new image which is the result of the seam carving. The right one is telling you which seams have been removed. 

### or
You can just type the size you want and press `enter` to start changing. like this: 

<img src="https://github.com/CYT823/SeamCarving/blob/master/git_images/sc3.png" width="600"/>

Inspired by a paper: <a href="https://dl.acm.org/doi/pdf/10.1145/1275808.1276390">Seam carving for content-aware image resizing</a>
