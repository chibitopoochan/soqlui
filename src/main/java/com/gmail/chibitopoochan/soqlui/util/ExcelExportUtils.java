package com.gmail.chibitopoochan.soqlui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellUtil;

import com.gmail.chibitopoochan.soqlexec.model.FieldMetaInfo;
import com.gmail.chibitopoochan.soqlui.config.Format;
import com.gmail.chibitopoochan.soqlui.model.DescribeField;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ExcelExportUtils {

	/**
	 * オブジェクト定義の出力
	 * @param list 項目定義
	 * @param name オブジェクト名
	 */
	public static void exportExcelFormat(List<DescribeField> list, String name) {

		// フォーマットを読み込み
		Format format = Format.getInstance();

		// ダイアログの生成
		File dir = DialogUtils.showDirectoryChooser();

		if(dir == null) return;

		// 項目のメタ情報を加工
		Set<String> keySet = new HashSet<>();
		Stream.of(FieldMetaInfo.Type.values()).forEach(t -> keySet.add("$"+t.name()));

		Workbook book = null;
		OutputStream out = null;
		InputStream input = null;
		try{
			input = new FileInputStream(new File(format.getFilePath()));
			book = WorkbookFactory.create(input);
			out = new FileOutputStream(Paths.get(dir.getAbsolutePath(), name + ".xlsx").toFile());
			// 開始地点のセルを走査
			Map<String,CellAddress> cellMap = new HashMap<>();
			Map<String,Sheet> sheetMap = new HashMap<>();
			for(Sheet s : book) {
				for(Row r : s) {
					for(Cell c : r) {
						if(c.getCellType() == CellType.STRING && keySet.contains(c.getStringCellValue())) {
							cellMap.put(c.getStringCellValue(), c.getAddress());
							sheetMap.put(c.getStringCellValue(), c.getSheet());
						}
					}
				}
			}

			// セルに値を埋め込む
			for(DescribeField field : list) {
				for(String key : keySet) {
					if(cellMap.containsKey(key)) {
						// 必要なデータを準備
						CellAddress a = cellMap.get(key);
						Sheet s = sheetMap.get(key);
						Row r = CellUtil.getRow(a.getRow(), s);
						Cell c = CellUtil.getCell(r, a.getColumn());

						// 値を設定
						c.setCellValue(field.getMetaInfo().get(FieldMetaInfo.Type.valueOf(key.substring(1))));

						// セルの位置を移動
						cellMap.put(key, new CellAddress(a.getRow()+1, a.getColumn()));
					}
				}
			}

			// ファイルを書き込み
			book.write(out);

			Alert confirm = new Alert(AlertType.INFORMATION, "Export finished.");
			confirm.showAndWait();

		} catch (Exception e) {
			e.printStackTrace();
			Alert confirm = new Alert(AlertType.INFORMATION, "Export failed.\n" + e.getMessage());
			confirm.showAndWait();
		} finally {
			try {
				input.close();
				out.close();
				book.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

	}

}
