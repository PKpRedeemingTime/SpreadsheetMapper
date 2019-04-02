package submissions.t5.pages;

import java.io.File;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;

import submissions.ValueObjects.BudgesBaseValueObject;
import submissions.ValueObjects.P1SpreadsheetColumn;
import submissions.ValueObjects.R1SpreadsheetColumn;
import submissions.data.PrcpType;
import submissions.service.P1R1Service;

public class FormatPrcpSpreadsheet {
	@Inject
	P1R1Service p1R1Service;
	@Persist
	@Property
	@Validate("required")
	private PrcpType prcpType;
	@Persist
	@Property
	private String selectedPrcp;
	@Persist
	@Property
	private Integer dataBeginsRow;
	@Persist
	@Property
	private String budgetYear;
	@Persist
	@Property
	private Boolean p1;
	@Persist
	@Property
	private Boolean r1;
	@Persist
	@Property
	private Boolean p1OrR1;
	@Persist
	@Property
	private Boolean skeleton;
	@Persist
	@Property
	private Boolean newColumn;
	@Persist
	@Property
	private Boolean updateColumn;
	@Persist
	@Property
	private P1SpreadsheetColumn p1Column;
	@Persist
	@Property
	private R1SpreadsheetColumn r1Column;
	@Property
	private P1SpreadsheetColumn p1ColumnRow;
	@Property
	private R1SpreadsheetColumn r1ColumnRow;
	@Property
	private String p1ColumnTitle;
	@Property
	private String p1ColumnLetter;
	@Property
	private int p1ColumnOrder;
	@Property
	private String r1ColumnTitle;
	@Property
	private String r1ColumnLetter;
	@Property
	private int r1ColumnOrder;
	@Property
	private int p1spreadsheetcolumn_id;
	@Property
	private List<P1SpreadsheetColumn> p1Columns;
	@Property
	private List<R1SpreadsheetColumn> r1Columns;
	@InjectComponent("p1UpdateColumn")
	private BeanEditForm form;
	@Property
	private UploadedFile copyFile;
	@Property
	private String copyFilePath;
	@Inject
    private AlertManager manager;
	
	void setupRender() {
		getAllP1s();
		getAllR1s();
	}
	
	Object onSuccessFromPrcpSelect() {
		if(prcpType.toString().equalsIgnoreCase("P1")) {
			selectedPrcp = "P1";
			p1 = true;
			p1OrR1 = true;
			r1 = false;
		} else if(prcpType.toString().equalsIgnoreCase("R1")) {
			selectedPrcp = "R1";
			r1 = true;
			p1OrR1 = true;
			p1 = false;
		}
		return this;
	}
	
	Object onSuccessFromP1Column() {
		p1R1Service.spreadsheetMapping(prcpType, p1ColumnTitle, p1ColumnOrder, p1ColumnLetter);
		newColumn = null;
		return this;
	}
	
	Object onSuccessFromR1Column() {
		p1R1Service.spreadsheetMapping(prcpType, r1ColumnTitle, r1ColumnOrder, r1ColumnLetter);
		newColumn = false;
		return this;
	}

	Object onSuccessFromAddP1Column() {
		newColumn = true;
		return this;
	}
	
	Object onSuccessFromAddR1Column() {
		newColumn = true;
		return this;
	}
	
	Object onActionFromP1Update(int id) {
		p1Column = (P1SpreadsheetColumn) p1R1Service.findOneP1R1Row(prcpType, id);
		updateColumn = true;
		return this;
	}
	
	Object onActionFromR1Update(int id) {
		r1Column = (R1SpreadsheetColumn) p1R1Service.findOneP1R1Row(prcpType, id);
		updateColumn = true;
		return this;
	}
	
	Object onSuccessFromP1UpdateColumn() {
		p1R1Service.updateColumn(prcpType, p1Column);
		updateColumn = false;
		return this;
	}
	
	Object onSuccessFromR1UpdateColumn() {
		p1R1Service.updateColumn(prcpType, r1Column);
		updateColumn = false;
		return this;
	}
	
	Object onActionFromP1Delete(int id) {
		p1R1Service.deleteColumn(prcpType, id);
		return this;
	}
	
	Object onActionFromR1Delete(int id) {
		p1R1Service.deleteColumn(prcpType, id);
		return this;
	}
	
	Object onActionFromCreateSkeleton() throws Exception {
		p1R1Service.buildSkeleton(prcpType, budgetYear);
		skeleton = true;
		return this;
	}
	
	Object onSuccessFromFileChooser() throws Exception {
		String myDocs = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
		File directory = new File(myDocs + File.separator  + "copyFromPRCP");
		directory.mkdir();
		File copyFromFile = new File(myDocs + File.separator  +  "copyFromPRCP" + File.separator  + copyFile.getFileName());
		copyFile.write(copyFromFile);
		String copyFromFilePath = copyFromFile.getPath();
		p1R1Service.populateData(prcpType, copyFromFilePath, budgetYear, dataBeginsRow);
		if(prcpType.toString().equalsIgnoreCase("P1")) {
			manager.alert(Duration.UNTIL_DISMISSED, Severity.INFO, "A data populated P1 PRCP spreadsheet has been created and stored in your Documents folder in a subfolder called 'completedPRCP'");
		} else {
			manager.alert(Duration.UNTIL_DISMISSED, Severity.INFO, "A data populated R1 PRCP spreadsheet has been created and stored in your Documents folder in a subfolder called 'completedPRCP'");
		}
		manager.alert(Duration.UNTIL_DISMISSED, Severity.INFO, "If you would like to create another spreadsheet, click the link below to start over");
		return this;
	}
	
	Object onSuccessFromRestart() {
		prcpType = null;
		selectedPrcp = null;
		p1 = null;
		r1 = null;
		skeleton = null;
		newColumn = null;
		updateColumn = null;
		p1Column = null;
		r1Column = null;
		p1OrR1 = null;
		dataBeginsRow = null;
		budgetYear = null;
		return this;
	}
	
	private void getAllP1s() {
		p1Columns = p1R1Service.getP1Columns();
	}
	
	private void getAllR1s() {
		r1Columns = p1R1Service.getR1Columns();
	}
	
}
