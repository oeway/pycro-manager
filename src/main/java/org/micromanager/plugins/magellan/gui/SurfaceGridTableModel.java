/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.org.micromanager.plugins.magellan.gui;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import main.java.org.micromanager.plugins.magellan.coordinates.XYStagePosition;
import main.java.org.micromanager.plugins.magellan.misc.Log;
import main.java.org.micromanager.plugins.magellan.misc.NumberUtils;
import main.java.org.micromanager.plugins.magellan.surfacesandregions.MultiPosGrid;
import main.java.org.micromanager.plugins.magellan.surfacesandregions.SurfaceGridListener;
import main.java.org.micromanager.plugins.magellan.surfacesandregions.SurfaceGridManager;
import main.java.org.micromanager.plugins.magellan.surfacesandregions.SurfaceInterpolator;
import main.java.org.micromanager.plugins.magellan.surfacesandregions.XYFootprint;

/**
 *
 * @author henrypinkard
 */
public class SurfaceGridTableModel extends AbstractTableModel implements SurfaceGridListener {

   private final String[] COLUMNS = {"Type", "Name", "Z Device", "# Positions"};

   private SurfaceGridManager manager_;

   public SurfaceGridTableModel() {
      manager_ = SurfaceGridManager.getInstance();
      manager_.registerSurfaceGridListener(this);
      //I suppose it never needs to be removed because this table is persisten as longa as magellan is open
   }

   @Override
   public int getRowCount() {
      return manager_.getNumberOfSurfaces() + manager_.getNumberOfGrids();
   }

   @Override
   public String getColumnName(int index) {
      return COLUMNS[index];
   }

   @Override
   public int getColumnCount() {
      return COLUMNS.length;
   }

   @Override
   public boolean isCellEditable(int rowIndex, int colIndex) {
      if (colIndex == 1) {
         return true;
      } else if (colIndex == 2 && manager_.getSurfaceOrGrid(rowIndex) instanceof SurfaceInterpolator) {
         return true; // only surfaces have XY padding
      }
      return false;
   }

   @Override
   public void setValueAt(Object value, int row, int col) {
      if (col == 1) {
         try {
            manager_.rename(row, (String) value);
         } catch (Exception ex) {
            Log.log("Name already taken by existing Surface/Grid", true);
         }
      } 
   }

   @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
//         private final String[] COLUMNS = {"Type", "Name", "XY padding (um)", "Z Device", "# Positions",
//      "# Rows", "# Cols", "Width (um)", "Height (um)"};
         
      XYFootprint surfaceOrGird = manager_.getSurfaceOrGrid(rowIndex);
      if (columnIndex == 0) {
         return manager_.getSurfaceOrGrid(rowIndex) instanceof SurfaceInterpolator ? "Surface"  : "Grid";
      } else if (columnIndex == 1) {         
         return manager_.getSurfaceOrGrid(rowIndex).getName();
      }  else if (columnIndex == 2) {
         if (manager_.getSurfaceOrGrid(rowIndex) instanceof MultiPosGrid) {
            return "N/A";
         }
         return ((SurfaceInterpolator) manager_.getSurfaceOrGrid(rowIndex)).getZDevice();
      } else {
         try {
            XYFootprint object = manager_.getSurfaceOrGrid(rowIndex);
            List<XYStagePosition> positions = object.getXYPositionsNoUpdate();
            return  positions != null ? positions.size() : 0; 
         } catch (InterruptedException ex) {
            Log.log(ex);
            return null;
         }

      } 
   }

   @Override
   public void SurfaceOrGridChanged(XYFootprint f) {
      fireTableDataChanged();
   }

   @Override
   public void SurfaceOrGridDeleted(XYFootprint f) {
      fireTableDataChanged();
   }

   @Override
   public void SurfaceOrGridCreated(XYFootprint f) {
      fireTableDataChanged();
   }

   @Override
   public void SurfaceOrGridRenamed(XYFootprint f) {
      fireTableDataChanged();
   }

   @Override
   public void SurfaceInterpolationUpdated(SurfaceInterpolator s) {
      //nothin
   }

}