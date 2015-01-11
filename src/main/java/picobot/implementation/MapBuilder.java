package picobot.implementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import picobot.interfaces.core.IMap;

public class MapBuilder implements picobot.interfaces.core.IMapBuilder {

  public void parseMap(String stringRepresentation) {
    _result = new Map();
    
    BufferedReader mapReader = new BufferedReader(new StringReader(stringRepresentation));

    // the first element of the list is the upper part of map
    // i.e. has a high index
    List<String> lines = new ArrayList<String>();    
    String aLine;
    try {
      aLine = mapReader.readLine();
      while (aLine != null) {
        lines.add(aLine);
        aLine = mapReader.readLine();
      }
    } catch (IOException e) {
      throw new SimulationException(e);
    }
    
    int yCoordinate = lines.size()-1;
    for (String line: lines) {
      int xCoordinate = 0;
      for (char s: line.toCharArray()) {
        if (s == '#') {
          _result.addWall(xCoordinate, yCoordinate);
          xCoordinate++;        
        }
        else if (s == ' ' || s == '\t') {
          xCoordinate++;        
        }
        else throw new SimulationException("Can not parse, sorry!");
      }
      yCoordinate--;
    }
    //System.out.println(_result);
  }
  
  public void modify(IMap map) {
	  _result = (Map)map;
  }
	  
  Map _result = new Map();

  public void createEmptySquareMap(int size) {
    // creating the north wall
    for (int i=0; i<size; i++) {
      Cell c = new Cell(i,size-1);
      c.setKind("WALL");
      _result.addCell(c);
    }
    
    // creating the south wall
    for (int i=0; i<size; i++) {
      Cell c = new Cell(i,0);
      c.setKind("WALL");
      _result.addCell(c);
    }

    // creating the west wall
    for (int i=0; i<size; i++) {
      Cell c = new Cell(0,i);
      c.setKind("WALL");
      _result.addCell(c);
    }

    // creating the east wall
    for (int i=0; i<size; i++) {
      Cell c = new Cell(size-1,i);
      c.setKind("WALL");
      _result.addCell(c);
    }
  }

  public void placeWall(int x, int y) {
    if (x<0 || y<0) { throw new IllegalArgumentException("x and y must be >= 0");}
    Cell c = new Cell(x,y);
    c.setKind("WALL");
    _result.addCell(c); 
  }

  public void placeFreeCell(int x, int y) {
    if (x<0 || y<0) { throw new IllegalArgumentException("x and y must be >= 0");}
    Cell c = new Cell(x,y);
    c.setKind("FREE");
    _result.addCell(c); 
  }

  public IMap getCurrentMap() {
    return _result;
  }

  /** Parse an ASCII representation of a map
   * that is stored in a file
   * 
   * This method generally calls {@link parseMap(String)}
   * 
   * @param a file object containing a map
   */
  public void parseMap(File mapFile) {
    try {
      BufferedReader fileReader = new BufferedReader(new FileReader(mapFile));
      Writer sw = new StringWriter();
      String s = fileReader.readLine(); 
      while (s != null) {
        // Warning the readLine removes the line break :-)
        sw.write(s+"\n");
        s = fileReader.readLine();      
      }
      parseMap(sw.toString());
    } catch (IOException e) {
      throw new SimulationException(e);    
    }
  }

  
}
