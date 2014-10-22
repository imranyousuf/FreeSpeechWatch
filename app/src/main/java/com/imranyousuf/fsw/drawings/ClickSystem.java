package com.imranyousuf.fsw.drawings;

import android.graphics.Canvas;
import android.util.Log;

import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class ClickSystem {
    private List<DrawingUndo> currentStack;
    private List<DrawingUndo> redoStack;

    public ClickSystem(){
        currentStack = Collections.synchronizedList(new ArrayList<DrawingUndo>());
        redoStack = Collections.synchronizedList(new ArrayList<DrawingUndo>());
    }

    public void addCommand(DrawingUndo command){
        redoStack.clear();
        currentStack.add(command);
    }

    public void undo (){
        final int length = currentStackLength();
        
        if ( length > 0) {
            final DrawingUndo undoCommand = currentStack.get(  length - 1  );
            currentStack.remove( length - 1 );
            undoCommand.undo();
            redoStack.add( undoCommand );
        }
    }

    public int currentStackLength(){
        final int length = currentStack.toArray().length;
        return length;
    }


    public void executeAll( Canvas canvas){
        if( currentStack != null ){
            synchronized( currentStack ) {
                final Iterator i = currentStack.iterator();
                Log.d("aaa",currentStack.toArray().length + "");
                while ( i.hasNext() ){
                    final DrawingUndo drawingUndo = (DrawingUndo) i.next();
                    drawingUndo.draw( canvas );
                }
            }
        }
    }



    public boolean hasMoreRedo(){
        return  redoStack.toArray().length > 0;
    }

    public boolean hasMoreUndo(){
        return  currentStack.toArray().length > 0;
    }

    public void redo(){
        final int length = redoStack.toArray().length;
        if ( length > 0) {
            final DrawingUndo redoCommand = redoStack.get(  length - 1  );
            redoStack.remove( length - 1 );
            currentStack.add( redoCommand );
        }
    }
}
