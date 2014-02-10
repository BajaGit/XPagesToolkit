package org.openntf.xpt.oneui.kernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import org.openntf.xpt.oneui.component.UINamePicker;

public enum NamePickerProcessor {
	INSTANCE;

	public String getTypeAhead(UINamePicker uiNp, String strSearch) {

		StringBuilder bRC = new StringBuilder();
		bRC.append("<ul><li><span class='informal'>Suggestions:</span></li>"); // $NON-NLS-1$
		try {
			Database db = DatabaseProvider.INSTANCE.getDatabase(uiNp.getDatabase());
			View vw = db.getView(uiNp.getView());
			DocumentCollection docCollection;
			if (db.isFTIndexed()) {
				try {

					db.updateFTIndex(true);
					vw.FTSearch(strSearch);

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
				}
				docProcess.recycle();
			}
			Collections.sort(lstNameEntries, new Comparator<NameEntry>() {

				@Override
				public int compare(NameEntry o1, NameEntry o2) {
					return o1.getLabel().compareTo(o2.getLabel());
				}

			});
			for (NameEntry nam : lstNameEntries) {

				int start = nam.getResultLine().toLowerCase().indexOf(strSearch.toLowerCase());
				int stop = start + 3 + strSearch.length();

				StringBuffer sb = new StringBuffer(nam.getResultLine());
				if (start > -1) {
					sb.insert(start, "<b>");
					sb.insert(stop, "</b>");
				}

				bRC.append("<li><a onclick=\"" + uiNp.buildJSFunctionCall(nam) + "\"><p>" + sb + "</span></p></a></li>");
			}
			bRC.append("</ul>"); // $NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println(result);
		return bRC.toString();
	}

	public HashMap<String, String> getDislplayLabels(UINamePicker uiNp, String[] values) {
		HashMap<String, String> hsRC = new HashMap<String, String>();
		try {
			Database db = DatabaseProvider.INSTANCE.getDatabase(uiNp.getDatabase());
			View vw = db.getView(uiNp.getView());
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
		// TODO Auto-generated method stub
		return hsRC;
	}
}