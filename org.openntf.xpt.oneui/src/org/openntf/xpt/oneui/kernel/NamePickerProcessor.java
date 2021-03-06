package org.openntf.xpt.oneui.kernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import org.openntf.xpt.core.utils.DatabaseProvider;
import org.openntf.xpt.oneui.component.UINamePicker;

import com.ibm.commons.util.StringUtil;

public enum NamePickerProcessor implements INamePickerValueService {
	INSTANCE;

	/* (non-Javadoc)
	 * @see org.openntf.xpt.oneui.kernel.INamePickerValueService#getTypeAheaderNE(org.openntf.xpt.oneui.component.UINamePicker, java.lang.String)
	 */
	@Override
	public List<NameEntry> getTypeAheadValues(UINamePicker uiNp, String strSearch) throws NotesException {
		Database db = DatabaseProvider.INSTANCE.getDatabase(uiNp.getDatabase(), false);
		View vw = db.getView(uiNp.getView());
		DocumentCollection docCollection;
		String strFTSearch = uiNp.buildFTSearch(strSearch);
		if (db.isFTIndexed() && !StringUtil.isEmpty(strFTSearch)) {
			try {

				db.updateFTIndex(true);
				vw.FTSearch(strFTSearch);

				ViewEntryCollection vecEntries = vw.getAllEntries();
				ViewEntry entryNext = vecEntries.getFirstEntry();
				docCollection = vw.getAllDocumentsByKey("EMPTY_COLLECTION"); // Initalize
																				// empty
																				// Collection

				while (entryNext != null) {
					ViewEntry entry = entryNext;
					entryNext = vecEntries.getNextEntry(entry);
					docCollection.addDocument(entry.getDocument());
					entry.recycle();
				}
				vecEntries.recycle();
			} catch (Exception e) {
				e.printStackTrace();
				docCollection = vw.getAllDocumentsByKey(strSearch, false);
			}
		} else {
			docCollection = vw.getAllDocumentsByKey(strSearch, false);
		}
		List<NameEntry> lstNameEntries = new ArrayList<NameEntry>();
		Document docNext = docCollection.getFirstDocument();
		while (docNext != null) {
			Document docProcess = docNext;
			docNext = docCollection.getNextDocument();
			NameEntry nam = uiNp.getDocumentEntryRepresentation(docProcess);
			if (nam != null) {
				lstNameEntries.add(nam);
				nam.buildResultLineHL(strSearch);
			}
			docProcess.recycle();
		}
		Collections.sort(lstNameEntries, new Comparator<NameEntry>() {

			@Override
			public int compare(NameEntry o1, NameEntry o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}

		});
		return lstNameEntries;
	}

	/* (non-Javadoc)
	 * @see org.openntf.xpt.oneui.kernel.INamePickerValueService#getDislplayLabels(org.openntf.xpt.oneui.component.UINamePicker, java.lang.String[])
	 */
	@Override
	public HashMap<String, String> getDislplayLabels(UINamePicker uiNp, String[] values) {
		HashMap<String, String> hsRC = new HashMap<String, String>();
		try {
			Database db = DatabaseProvider.INSTANCE.getDatabase(uiNp.getDatabase(), false);
			View vw = null;
			if (StringUtil.isEmpty(uiNp.getLookupView())) {
				vw = db.getView(uiNp.getView());
			} else {
				vw = db.getView(uiNp.getLookupView());

			}
			for (String strValue : values) {
				// Assign a default value
				hsRC.put(strValue, strValue);
				if (vw != null) {
					Document docRC = vw.getDocumentByKey(strValue, true);
					if (docRC != null) {
						String strLabel = uiNp.getDisplayLableValue(docRC);
						hsRC.put(strValue, strLabel);
					}
					docRC.recycle();
				}
			}
			if (vw != null) {
				vw.recycle();
			}
			DatabaseProvider.INSTANCE.handleRecylce(db);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return hsRC;
	}
}
