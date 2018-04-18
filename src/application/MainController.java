package application;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.sun.javafx.webkit.ThemeClientImpl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainController {

	@FXML
	private Button single, multi, btn_delete, btn_open, btn_newFolder, ok, btn_selectFiles, btn_close, btn_move;
	
	
	@FXML
	private ListView listview;
	
	@FXML
	private TextField textfield;
	
	@FXML
	private TextField pathText;
	
	@FXML
	private MenuBar menuBar;
	
	@FXML
	private MenuItem clearSelected;		
	
	File selected;
	List<File> selectedFiles;
	List<File> lvSelectedFiles;
	ArrayList<String> toDelete = new ArrayList<>();
	String name = new String();
	String path = new String();
	String pathToOpen = new String();
	String toMoveListName = new String();
	ArrayList<Integer> selectedItems = new ArrayList<>();
	ArrayList<String> selectedItemsPath = new ArrayList<>();
	ArrayList<Integer> ctrlList = new ArrayList<>();
	ArrayList<String> toDeleteList = new ArrayList<>();
	ArrayList<String> listnames = new ArrayList<>();
	ArrayList<String> toMoveList = new ArrayList<>();
	
	int flag, shiftFlag = 0, ctrlFlag = 0, m, n;
	Stage stage = new Stage();
	int selectedIndex;
	
	public void singleAction(ActionEvent event){
		FileChooser fileChooser = new FileChooser();
		selected = fileChooser.showOpenDialog(null);
		
		if(selected != null){
			listview.getItems().add(selected.getName());
			toDelete.add(selected.getAbsolutePath());
			listnames.add(selected.getName());
		}
		else{
			
		}
	}
	
	public void multiAction(ActionEvent event){
		FileChooser fileChooser = new FileChooser();
		selectedFiles = fileChooser.showOpenMultipleDialog(null);
		
		if(selectedFiles != null){
			for(int i = 0; i<selectedFiles.size(); i++){
				listview.getItems().add(selectedFiles.get(i).getName());
				toDelete.add(selectedFiles.get(i).getAbsolutePath());
				listnames.add(selectedFiles.get(i).getName());
			}
		}
		else{
			
		}
	}
	
	public void onListView(MouseEvent mouseEvent){		
		listview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		selectedIndex = listview.getSelectionModel().getSelectedIndex();
		
		btn_delete.setDisable(false);
		btn_move.setDisable(false);
		
		if(shiftFlag == 1){
			selectedItems.add(selectedIndex);
			m = selectedItems.get(selectedItems.size() - 1);
			n = selectedItems.get(selectedItems.size() - 2);
			
			selectedItems.remove(selectedItems.size() - 1);
			
			for(int i = n + 1; i<=m; i++){
				File file = new File(toDelete.get(i));
				selectedItems.add(i);
				selectedItemsPath.add(file.getAbsolutePath());
			}
		}
		else if(ctrlFlag == 1){
			selectedItems.add(selectedIndex);
			File file = new File(toDelete.get(selectedIndex));
			selectedItemsPath.add(file.getAbsolutePath());
		}
		else{
			selectedItems.clear();
			selectedItemsPath.clear();
			
			selectedItems.add(selectedIndex);
			clearSelected.setDisable(false);
			
			pathText.setText(toDelete.get(selectedIndex));
			selectedItemsPath.add(pathText.getText());
			
		}
		
	}
	
	public void onListViewKey(KeyEvent k){
		if(k.getCode().equals(KeyCode.SHIFT)){
			shiftFlag = 1;
		}
		else if(k.getCode().equals(KeyCode.CONTROL)){
			ctrlFlag = 1;
		}
		else {
			shiftFlag = 0;
			ctrlFlag = 0;
		}
	}
	
	public void onListViewKey2(KeyEvent k){
		shiftFlag = 0;
		ctrlFlag = 0;
	}
	
	public void onDeleteBtn(MouseEvent mouseEvent){
		
		
		if(selectedItems.size() == 0){
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("Please select files before trying to delete.");
			alert.setHeaderText(null);
			alert.setTitle("Information");
			alert.showAndWait();
			
			return;
		}

		File file;
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setContentText("Do you want to permanently delete selected file(s)?");
		alert.setHeaderText(null);
		
		Optional<ButtonType> result = alert.showAndWait();
		
		if(result.get() == ButtonType.OK){
			
				int a = 0;
				for(int i: selectedItems){
					file = new File(selectedItemsPath.get(a));
					file.delete();
					listnames.remove(file.getName());
					toDelete.remove(file.getAbsolutePath());
					pathText.clear();
					a++;
					
					
				}
				
				listview.getItems().clear();
				for(String i: listnames){
					listview.getItems().add(i);
				}
				
			
		}
	}
	
	
	public void onOpen(ActionEvent event){
		
		if(selectedItems.size() != 0){
			File file = new File(selectedItemsPath.get(0));
			try {
				Desktop.getDesktop().open(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			singleAction(null);
			try {
				Desktop.getDesktop().open(selected);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void onNewFolder(MouseEvent event){
		
		try{
			
			onNameSelect();
			
		}catch (Exception e) {
			
		}
		
		
	}
	
	public void onOk(MouseEvent event){
		name = textfield.getText().toString();

		if(name.length() != 0){
			DirectoryChooser dChooser = new DirectoryChooser();
			File selectedDirectory = dChooser.showDialog(null);
			path = selectedDirectory.getAbsolutePath().toString();
			//File file = new File(path + "\\" + name);
			new File(path + "\\" + name).mkdirs();
			
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Success");
			alert.setHeaderText(null);
			alert.setContentText("The folder creation is successful.");
			alert.show();
			
			stage = (Stage) ok.getScene().getWindow();
			stage.close();
		}
	}
	
	public void onNameSelect(){
		Parent root = null;
		try {
			root = FXMLLoader.load(Main.class.getResource("/application/FolderName.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(root,400,200);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setTitle("Name of the Folder");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}
	
	public void onSelectFiles(MouseEvent event){
		
		DirectoryChooser chooser = new DirectoryChooser();
		File selectedDirectory = chooser.showDialog(null);
		
		File[] listOfFiles = selectedDirectory.listFiles();
		
		for(int f = 0; f<listOfFiles.length; f++){
			listview.getItems().add(listOfFiles[f].getName());
			toDelete.add(listOfFiles[f].getAbsolutePath());
			listnames.add(listOfFiles[f].getName());
		}
		
	}
	
	public void onBtnClose(MouseEvent event){
		stage = (Stage) btn_close.getScene().getWindow();
		stage.close();
	}
	
	public void onKey(KeyEvent k){
		if(k.getCode().equals(KeyCode.ENTER)){
			if(pathText.getText().length() == 0){
				pathText.setText("Please enter a path");
			}
			else {
				File file = new File(pathText.getText());
				try{
					Desktop.getDesktop().open(file);
					
				}catch (Exception e) {
					pathText.setText("Please enter a valid path");
				}
			}
		}
	}
	
	public void onMenuClose(ActionEvent event){
		onBtnClose(null);
	}
	
	public void onMenuNew(ActionEvent event){
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec("notepad");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onMenuOpen(ActionEvent event){
		FileChooser fileChooser = new FileChooser();
		
	}
	
	public void onMenuClear(ActionEvent event){
		listview.getItems().clear();
		toDelete.clear();
		pathText.clear();
		selectedItems.clear();
		selectedItemsPath.clear();
		listnames.clear();
		btn_delete.setDisable(true);
		
	}
	
	
	public void onMenuClearSelected(ActionEvent event){
		if(listview.getItems().size() != 0){
			for(int i: selectedItems){				
				listnames.remove(listview.getItems().get(i));
			}
			
			selectedItems.clear();
			selectedItemsPath.clear();
			pathText.clear();
			toDelete.clear();
			listview.getItems().clear();
			
			for(String i: listnames){
				listview.getItems().add(i);
				toDelete.add(i);
			}
		}
		
		if(listnames.size() == 0){
			btn_delete.setDisable(true);
		}
		
	}
	
	public void onMove(MouseEvent event){
		
		if(selectedItems.size() == 0){
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("Please select files before trying to move.");
			alert.setHeaderText(null);
			alert.setTitle("Information");
			alert.showAndWait();
			
			return;
		}
		
		File file;
		Path path, path2;
		
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedDirectory = directoryChooser.showDialog(null);
		
		path2 = Paths.get(selectedDirectory.getAbsolutePath());
		
		toMoveList.clear();
		for(int i: selectedItems){
			System.out.println("size: " + toDelete.size());
			System.out.println(i);
			
			file = new File(toDelete.get(i));
			toMoveListName = file.getName();
			path = Paths.get(file.getAbsolutePath());
			path2 = Paths.get(selectedDirectory.getAbsolutePath() + "\\" + toMoveListName);
			listnames.remove(toMoveListName);
			toMoveList.add(toDelete.get(i));
			try {
				Files.move(path, path2, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		listview.getItems().clear();
		for(String i: listnames){
			listview.getItems().add(i);
		}
		
		for(String i: toMoveList){
			toDelete.remove(i);
		}
		
	}
	
}
