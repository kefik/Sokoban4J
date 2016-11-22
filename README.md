# Sokoban4J
Sokoban for Java (using Swing) tailored for casual playing but especially for creating custom Sokoban agents. Fully playable but truly meant for programmers for the development of Sokoban artificial players.

Supports load of .sok packages from  [sokobano.de](http://sokobano.de/en/levels.php); many .sok packages downloaded within Sokoban4J/levels/sokobano.de folder. Thank you for such wonderful collection!

Art used under [CC-BY-SA-4.0](https://creativecommons.org/licenses/by-sa/4.0/legalcode) downloaded from [OpenGameArt](http://opengameart.org/content/sokoban-pack) created by 1001.com; thank you!

**LICENSED UNDER** [CC-BY-SA-4.0](https://creativecommons.org/licenses/by-sa/4.0/legalcode) Please retain URL to the [Sokoban4J](https://github.com/kefik/Sokoban4J) in your work.

![alt tag](https://github.com/kefik/Sokoban4J/raw/master/Sokoban4J/screenshot.png)

## FEATURES

1) Two different representation of the game state - see Board for OOP representation used by the simulator and then BoardCompact that should be used for state space search.

2) HumanAgent and ArtificialAgent stubs; ArtificialAgent is using own thread for thinking (does not stuck GUI). See DFSAgent from Sokoban4J-Agents project for the example of artificial player.

3) Character is animated; human may use arrows or WSAD to control the avatar.

4) Possible to run headless simulations (same result as visualized, only faster).

5) Use "Sokoban" static methods for quick startups of your code (both for humans and artificial agents).

6) Simple Tree-DFS agent that can solve the simplest level.

7) You can define up-to 6 different kinds of "boxes" (yellow, blue, gray, purple, red and black) and their specific target places; brown place (the brown dot) is "generic spot for any kind of box".

8) Mavenized (repo and dependency at the end of the page); uses some of my other stuff, but that can be easily cut off if you're considering branching.

9) Tested with Java 1.8, compilable with 1.6 as well.

10) Using pure Swing, no complexities in there...

11) Introductory tips for creating Sokoban artificial player in this [report](http://pavel.klavik.cz/projekty/solver/solver.pdf) (courtesy of Pavel Klavík).

12) Load of .sok level packages from [sokobano.de](http://sokobano.de/) fully supported; many packages from http://sokobano.de/en/levels.php were downloaded into Sokoban4J/levels/sokobano.de (Thank you for such wonderful collection!)

13) Large levels automatically scales down to fit the screen in order to be playable by humans.

------------------------------------------------------------

![alt tag](https://github.com/kefik/Sokoban4J/raw/master/Sokoban4J/screenshot2.png)

------------------------------------------------------------

##PROJECT STRUCTURE

**Sokoban4J** -> main project containing the simulator and visualizer of the game

**Sokoban4J-Agents** -> example artificial agents for Sokoban4J, so far only Tree DFS in here

**Sokoban4J-Playground** -> meant for easy hop-on the train of Sokoban agent development; fool around in here rather than Sokoban4J-Agents that should serve for you as backup ;)

##COMPILATION

Compile Sokoban4J project (from within Sokoban4J directory):

Windows (from cmd; assuming you have mvn on path):

    mvn package
    
Linux (from bash, assuming you have mvn on path):

    mvn package

##MAVEN [REPOSITORY](http://diana.ms.mff.cuni.cz:8081/artifactory)

    <repository>
        <id>amis-artifactory</id>
        <name>AMIS Artifactory</name>
        <url>http://diana.ms.mff.cuni.cz:8081/artifactory/repo</url>
    </repository>
    
##MAVEN DEPENDENCY

    <dependency>
        <groupId>cz.sokoban4j</groupId>
        <artifactId>sokoban4j</artifactId>
        <version>0.1.0-SNAPSHOT</version>
    </dependency>

    

    
