package nist.squire.guipanels;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.micromanager.api.MultiStagePosition;
import org.micromanager.api.PositionList;
import org.micromanager.api.StagePosition;
import org.micromanager.hcs.HCSException;
import org.micromanager.hcs.SBSPlate;
import org.micromanager.hcs.WellPositionList;
import org.micromanager.utils.ReportingUtils;

public class NISTPlate extends SBSPlate {

	private int numColumns_;
	private int numRows_;
	private double wellSpacingX_;
	private double wellSpacingY_;
	private double sizeXUm_;
	private double sizeYUm_;
	private double firstWellX_;
	private double firstWellY_;
	private double wellSizeX_;
	private double wellSizeY_;
	private boolean circular_ = true;
	private SBSPlate superPlate;

	private String id_;
	private String description_;
	private HashMap<String, Well> wellMap_;

	public static final String NIST_6_WELL= "6 Well";
	public static final String NIST_12_WELL= "12 Well";
	public static final String NIST_12_WELL_TRANS = "12 Well - Transwells";
	public static final String NIST_24_WELL= "24 Well";
	public static final String NIST_48_WELL= "48 Well";
	public static final String NIST_96_WELL= "96 Well";
	public static final String NIST_384_WELL= "384 Well";
	public static final String NIST_SLIDE_HOLDER ="Slides";
	public static final String NIST_SINGLE_SLIDE = "Single Slide";
	public static final String NIST_CUSTOM = "Custom";

	private static final String ROWS = "rows";
	private static final String COLS = "cols";
	private static final String WELL_SPACING_X = "well_spacing_X";
	private static final String WELL_SPACING_Y = "well_spacing_Y";
	private static final String PLATE_SIZE_X = "plate_size_X";
	private static final String PLATE_SIZE_Y = "plate_size_Y";
	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static final String FIRST_WELL_X = "first_well_x";
	private static final String FIRST_WELL_Y = "first_well_y";

	private static final String METADATA_SITE_PREFIX = "Site";

	private static final char rowAlphabet[] = { 'A','B','C','D','E',
		'F','G','H','I','J',
		'K','L','M','N','O',
		'P','Q','R','S','T',
		'U','V','W','X','Y','Z' };

	public NISTPlate() {
		super();
		wellMap_ = new HashMap<String, Well>();
	}

	@Override
	public String toString() {
		return id_;
	}

	public final void initializePlate(String id) {

		if (id.equals(NIST_SINGLE_SLIDE)){
			id_ = NIST_SINGLE_SLIDE;
			numColumns_ = 1;
			numRows_ = 1;
			sizeXUm_ = 127760.0;
			sizeYUm_ = 85470.0;
			wellSpacingX_ = 80000.0;
			wellSpacingY_ = 27600.0;
			firstWellX_ = 40000.0;
			firstWellY_ = 20000.0;
			wellSizeX_ = 75000.0;
			wellSizeY_ = 25600.0;
			circular_ = false;
		} else if (id.equals(NIST_6_WELL)){
			id_ = NIST_6_WELL;
			numColumns_ = 3;
			numRows_ = 2;
			sizeXUm_ = 127760.0;
			sizeYUm_ = 85470.0;
			wellSpacingX_ = 39120.0;
			wellSpacingY_ = 39120.0;
			firstWellX_ = 24760.0;
			firstWellY_ = 23160.0;
			wellSizeX_ = 34800.0;
			wellSizeY_ = 34800.0;
			circular_ = true;
		} else if (id.equals(NIST_12_WELL)) {
			id_ = NIST_12_WELL;
			numColumns_ = 4;
			numRows_ = 3;
			sizeXUm_ = 127890.0;
			sizeYUm_ = 85600.0;
			wellSpacingX_ = 26010.0;
			wellSpacingY_ = 26010.0;
			firstWellX_ = 24940.0;
			firstWellY_ = 16790.0;
			wellSizeX_ = 22110.0;
			wellSizeY_ = 22110.0;
			circular_ = true;
		} else if (id.equals(NIST_12_WELL_TRANS)) {
			id_ = NIST_12_WELL_TRANS;
			numColumns_ = 4;
			numRows_ = 3;
			sizeXUm_ = 127890.0;
			sizeYUm_ = 85600.0;
			wellSpacingX_ = 26010.0;
			wellSpacingY_ = 26010.0;
			firstWellX_ = 24940.0;
			firstWellY_ = 16790.0;
			wellSizeX_ = 12000.0;
			wellSizeY_ = 12000.0;
			circular_ = true;
		} else if (id.equals(NIST_24_WELL)){
			id_ = NIST_24_WELL;
			numColumns_ = 6;
			numRows_ = 4;
			sizeXUm_ = 127500.0;
			sizeYUm_ = 85250.0;
			wellSpacingX_ = 19300.0;
			wellSpacingY_ = 19300.0;
			firstWellX_ = 17050.0;
			firstWellY_ = 13670.0;
			wellSizeX_ = 15540.0;
			wellSizeY_ = 15540.0;
			circular_ = true;
		} else if (id.equals(NIST_48_WELL)){
			id_ = NIST_48_WELL;
			numColumns_ = 8;
			numRows_ = 6;
			sizeXUm_ = 127760.0;
			sizeYUm_ = 85480.0;
			wellSpacingX_ = 13000.0;
			wellSpacingY_ = 13000.0;
			firstWellX_ = 18380.0;
			firstWellY_ = 10240.0;
			wellSizeX_ = 11370.0;
			wellSizeY_ = 11370.0;
			circular_ = true;
		} else if (id.equals(NIST_96_WELL)){
			id_ = NIST_96_WELL;
			numColumns_ = 12;
			numRows_ = 8;
			sizeXUm_ = 127760.0;
			sizeYUm_ = 85480.0;
			wellSpacingX_ = 9000.0;
			wellSpacingY_ = 9000.0;
			firstWellX_ = 14380.0;
			firstWellY_ = 11240.0;
			wellSizeX_ = 8000.0;
			wellSizeY_ = 8000.0;
			circular_ = true;
		} else if (id.equals(NIST_384_WELL)){
			id_ = NIST_384_WELL;
			numColumns_ = 24;
			numRows_ = 16;
			sizeXUm_ = 127760.0;
			sizeYUm_ = 85480.0;
			wellSpacingX_ = 4500.0;
			wellSpacingY_ = 4500.0;
			firstWellX_ = 12130.0;
			firstWellY_ = 8990.0;
			wellSizeX_ = 4000.0;
			wellSizeY_ = 4000.0;
			circular_ = false;
		} else if (id.equals(NIST_SLIDE_HOLDER)) {
			id_ = NIST_SLIDE_HOLDER;
			numColumns_ = 4;
			numRows_ = 1;
			sizeXUm_ = 127760.0;
			sizeYUm_ = 85480.0;
			wellSpacingX_ = 27600.0;
			wellSpacingY_ = 80000.0;
			firstWellX_ = 20000.0;
			firstWellY_ = 50000.0;
			wellSizeX_ = 25600.0;
			wellSizeY_ = 75000.0;
			circular_ = false;
		}

		super.setWellSpacingY(wellSpacingY_);
		super.setWellSpacingX(wellSpacingX_);
		super.setFirstWellY(firstWellY_);
		super.setFirstWellX(firstWellX_);
		super.setNumColumns_(numColumns_);
		super.setNumRows(numRows_);
		super.setWellSpacingX(wellSpacingX_);
		super.setWellSpacingY(wellSpacingY_);
		super.setSizeY(sizeYUm_);
		super.setSizeX(sizeXUm_);
		
		try {
			generateWells();
		} catch (HCSException e) {
			ReportingUtils.logError(e);
		}

	}

	public void load(String path) throws HCSException {
		StringBuilder contents = new StringBuilder();
		try {
			// read metadata from file
			BufferedReader input = new BufferedReader(new FileReader(path));
			String line;
			while (( line = input.readLine()) != null){
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
			input.close();
			restore(contents.toString());
		} catch (IOException e) {
			throw new HCSException(e);
		}

	}

	public void save(String path) throws HCSException {
		try {
			FileWriter fw = new FileWriter(path);
			fw.write(serialize());
			fw.close();
		} catch (IOException e) {
			throw new HCSException("Unable to create plate definition file: " + e.getMessage());
		}
	}

	public String serialize() throws HCSException {
		JSONObject plate = new JSONObject();
		try {
			plate.put(ROWS, numRows_);
			plate.put(COLS, numColumns_);
			plate.put(WELL_SPACING_X, wellSpacingX_);
			plate.put(WELL_SPACING_Y, wellSpacingY_);
			plate.put(PLATE_SIZE_X, sizeXUm_);
			plate.put(PLATE_SIZE_Y, sizeYUm_);
			plate.put(ID, id_);
			plate.put(DESCRIPTION, description_);
			plate.put(FIRST_WELL_X, firstWellX_);
			plate.put(FIRST_WELL_Y, firstWellY_);
			return plate.toString(3);
		} catch (JSONException e) {
			throw new HCSException(e);
		}
	}

	public void restore(String ser) throws HCSException {

		JSONObject plate;
		try {
			plate = new JSONObject(ser);
			numRows_ = plate.getInt(ROWS);
			numColumns_ = plate.getInt(COLS);
			wellSpacingX_ = plate.getInt(WELL_SPACING_X);
			wellSpacingY_ = plate.getInt(WELL_SPACING_Y);
			sizeXUm_ = plate.getInt(PLATE_SIZE_X);
			sizeYUm_ = plate.getInt(PLATE_SIZE_Y);
			id_ = plate.getString(ID);
			description_ = plate.getString(DESCRIPTION);
			firstWellX_ = plate.getDouble(FIRST_WELL_X);
			firstWellY_ = plate.getDouble(FIRST_WELL_Y);
		} catch (JSONException e) {
			throw new HCSException(e);
		}
	}

	/**
	 * Generate a list of well positions using 'snake' pattern.
	 * Takes a list of sites and merges them into the well list.
	 * Site XY coordinates are assumed to be relative to the well center.
	 * @param xyStageName name of the XY stage used to generate these sites
	 * @param sites 
	 * @return - an array of well positions
	 */
	public WellPositionList[] generatePositions(String xyStageName, PositionList sites) {
		WellPositionList posListArray[] = new WellPositionList[numRows_ * numColumns_];
		boolean direction = true;
		int wellCount = 0;

		for (int i=0; i<numRows_; i++) {
			for (int j=0; j<numColumns_; j++) {
				WellPositionList wpl = new WellPositionList();
				String wellLabel;
				int colIndex;
				if (direction)
					colIndex = j+1; // forward
				else
					colIndex = numColumns_ - j; // reverse
				wellLabel = getWellLabel(i+1, colIndex);

				try {
					double wellX = getWellXUm(wellLabel);
					double wellY = getWellYUm(wellLabel);
					PositionList absSites = new PositionList();
					if (i==0 && j==0) {
						MultiStagePosition absMps = new MultiStagePosition();
						absMps.setLabel("BLANKWELL");
						wpl.setGridCoordinates(i, colIndex-1);
						absMps.setDefaultXYStage(xyStageName);
						StagePosition absSp = new StagePosition();
						absSp.x = wellX;
						absSp.y = wellY;
						absSp.stageName = xyStageName;
						absSp.numAxes = 2;
						absMps.add(absSp);
						absSites.addPosition(absMps);
					} else {
						for (int k=0; k<sites.getNumberOfPositions(); k++) {
							MultiStagePosition mps = sites.getPosition(k);
							MultiStagePosition absMps = new MultiStagePosition();
							wpl.setGridCoordinates(i, colIndex-1);
							absMps.setDefaultXYStage(xyStageName);
							// TODO: make sure we get the right XY stage not just the first one
							StagePosition sp = mps.get(0);
							StagePosition absSp = new StagePosition();
							absSp.x = wellX + sp.x;
							absSp.y = wellY + sp.y;
							String formatRow = String.format("%03d", mps.getGridRow());
							String formatCol = String.format("%03d", mps.getGridColumn());
							absMps.setLabel("r" + formatRow + "_c" + formatCol);
							absSp.stageName = xyStageName;
							absSp.numAxes = 2;
							absMps.add(absSp);
							absSites.addPosition(absMps);
						}
					}
					wpl.setSitePositions(absSites);
					wpl.setLabel(wellLabel);
					posListArray[wellCount++] = wpl;
				} catch (HCSException e) {
					// TODO Auto-generated catch block
					ReportingUtils.logError(e);
				}
			}
			direction = !direction; // reverse direction
		}
		return posListArray;
	}

	public WellPositionList[] generatePositions(String xyStageName) {

		// generate default site in the center of the well
		PositionList sites = new PositionList();
		MultiStagePosition mps = new MultiStagePosition();
		StagePosition sp = new StagePosition();
		sp.numAxes = 2;
		sp.x = 0.0;
		sp.y = 0.0;
		mps.add(sp);
		sites.addPosition(mps);

		return generatePositions(xyStageName, sites);
	}

	public String getID() {
		return id_;
	}

	public String getDescription() {
		return description_;
	}

	public double getWellXUm(String wellLabel) throws HCSException {
		if (wellMap_.containsKey(wellLabel))
			return wellMap_.get(wellLabel).x;

		throw new HCSException("Invalid well label: " + wellLabel);
	}

	public double getWellYUm(String wellLabel) throws HCSException {
		if (wellMap_.containsKey(wellLabel))
			return wellMap_.get(wellLabel).y;

		throw new HCSException("Invalid well label: " + wellLabel);
	}

	public String getColumnLabel(int col) {

		if(col < 1 || col > numColumns_)
			//throw new HCSException("Invalid column number: " + col);
			return "";

		return Integer.toString(col);
	}

	public String getRowLabel(int row) {
		// limit row index to valid range
		if (row < 1 || row > numRows_)
			//throw new HCSException("Invalid row number: " + row);
			return "";

		// build the row label
		int tempRow = row;
		String label = new String();
		while( tempRow > 0 )
		{
			int letterIndex = (tempRow - 1) % rowAlphabet.length;
			label += rowAlphabet[letterIndex];
			tempRow = ( tempRow - 1 ) / rowAlphabet.length;
		}
		return label;
	}

	public String getWellLabel(int row, int col){
		return getRowLabel(row) + getColumnLabel(col);
	}

	private void generateWells() throws HCSException {
		wellMap_.clear();
		for (int i=0; i<numRows_; i++)
			for (int j=0; j<numColumns_; j++) {
				Well w = new Well();
				w.x = firstWellX_ + wellSpacingX_ * j;
				w.y = firstWellY_ + wellSpacingY_ * i;
				w.row = i+1;
				w.col = j+1;
				w.label = getWellLabel(w.row, w.col);
				wellMap_.put(w.label, w);
			}
	}

	private class Well {
		public String label;
		public int row;
		public int col;
		public double x;
		public double y;

		public Well() {
			row = 0;
			col = 0;
			x = 0.0;
			y = 0.0;
			label = "Undefined";
		}
	}

	public int getNumRows() {
		return numRows_;
	}

	public int getNumColumns() {
		return numColumns_;
	}

	public double getXSize() {
		return sizeXUm_;
	}

	public double getYSize() {
		return sizeYUm_;
	}

	public double getTopLeftX() {
		return firstWellX_ - wellSpacingX_ / 2.0;
	}

	public double getTopLeftY() {
		return firstWellY_ - wellSpacingY_ / 2.0;
	}

	public double getBottomRightX() {
		return firstWellX_ + wellSpacingX_ * (numColumns_ - 1) + wellSpacingX_ / 2.0;
	}

	public double getBottomRightY() {
		return firstWellY_ + wellSpacingY_ * (numRows_ - 1) + wellSpacingY_ / 2.0;
	}

	public void setNumColumns_(int numColumns_) {
		id_ = NIST_CUSTOM;
		this.numColumns_ = numColumns_;
	}

	public void setNumRows(int numRows) {
		id_ = NIST_CUSTOM;
		this.numRows_ = numRows;
	}

	public double getWellSpacingX() {
		return wellSpacingX_;
	}

	public void setWellSpacingX(double wellSpacingX) {
		id_ = NIST_CUSTOM;
		this.wellSpacingX_ = wellSpacingX;
	}

	public double getWellSpacingY() {
		return wellSpacingY_;
	}

	public void setWellSpacingY(double wellSpacingY) {
		id_ = NIST_CUSTOM;
		this.wellSpacingY_ = wellSpacingY;
	}

	public double getWellSizeX() {
		return wellSizeX_;
	}

	public double getWellSizeY() {
		return wellSizeY_;
	}

	public void setSizeX(double sizeXUm) {
		id_ = NIST_CUSTOM;
		this.sizeXUm_ = sizeXUm;
	}

	public void setSizeY(double sizeYUm) {
		id_ = NIST_CUSTOM;
		this.sizeYUm_ = sizeYUm;
	}

	public double getFirstWellX() {
		return firstWellX_;
	}

	public void setFirstWellX(double firstWellX) {
		id_ = NIST_CUSTOM;
		this.firstWellX_ = firstWellX;
	}

	public double getFirstWellY() {
		return firstWellY_;
	}

	public void setFirstWellY(double firstWellY) {
		id_ = NIST_CUSTOM;
		this.firstWellY_ = firstWellY;
	}

	public boolean isWellCircular() {
		return circular_;
	}

	public String getWellLabel(double x, double y) {
		int col = getColumn(x);
		int row = getRow(y);
		return getWellLabel(row+1, col+1);
	}

	public int getRow(double y) {
		return (int)((y - getTopLeftY())/wellSpacingY_);
	}

	public int getColumn(double x) {
		return (int)((x - getTopLeftX())/wellSpacingX_);
	}

	public boolean isWithin(double x, double y) {
		return x >= 0.0 && x < sizeXUm_ && y >= 0.0 && y < sizeYUm_;

	}

}
