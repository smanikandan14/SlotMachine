SlotMachine
===========

Tried out the following coding challege to implement a slotmachine to get coffee, tea or espresso

### Challenge

Employees enjoy a healthy dose of caffeine in the morning. They like to drink coffee, tea, or espresso. Goal is to build a slot machine to award lucky users one of these random beverages.

Slot machine should have three reels:

1. The first reel has a coffee maker, a teapot, and an espresso machine.
2. The second reel has a coffee filter, a tea strainer, and an espresso tamper.
3. The third reel has coffee grounds, loose tea, and ground espresso beans.

When the lever is pulled (you can simulate this with the click of a button), the slot machine goes into action. Each reel spins and randomly stops on one of the three options. If the user is lucky, the three reels will line up and she will be rewarded with a tasty hot beverage. Your solution should show the user what beverage she's won. For example, if the reels show coffee maker, coffee filter, and coffee grounds, the user wins a cup of coffee.

###### Portrait


![alt text](https://github.com/smanikandan14/SlotMachine/blob/master/images/slotmachine_portrait.png "SlotMachine Portrait")

###### Landscape


![alt text](https://github.com/smanikandan14/SlotMachine/blob/master/images/slotmachine_landscape.png "SlotMachine Landscape")

###### Design In Detail
![alt text](https://github.com/smanikandan14/SlotMachine/blob/master/images/scroller.png "SlotMachine Immplementation")

#### WheelView
* Since the slot machine reels scrolls in vertical directions ( can be top to bottom or bottom to top ) we need to draw slot items with its position Y values kept incremented or decremented depending on direction.
* To compute the scroll value over a time duration 'Scroller' is used. 
* Ask the scroller to generate scroll offset values for from Y '0' to Y 'distance'
  ```java
	mScroller.startScroll(0, 0, 0, distance, duration); 
	```
* use computeScrollOffset() and getCurrY() to obtain a offset (delta) values which is used to draw the slot items on the 
* view canvas giving the visual feel of scrolling.

### Cyclic scrolling
* In order to bring the cyclic scrolling effect, the top element when goes out of screen is added back to the slotitems list, basically reordering the list such that scrolling appears cyclic.

### canvas onDraw
* Draw frames 
* Draw a white color background to entire view
* Draw the slot items one by one with its positionY value.
* Draw top and bottom shadows to bring a illusion that slot reels are circular in shape.
