package com.example.starfishapp;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class MultiSelectionSpinner extends Spinner implements
  OnMultiChoiceClickListener {
  String[] names = null;
  boolean[] selection = null;
//  List<T> items = new ArrayList<T>();
  ArrayAdapter<String> adapter;

  public MultiSelectionSpinner(Context context) {
    super(context);
    adapter = new ArrayAdapter<String>(context,
      android.R.layout.simple_spinner_item);
    super.setAdapter(adapter);
  }

  public MultiSelectionSpinner(Context context, AttributeSet attrs) {
    super(context, attrs);
    adapter = new ArrayAdapter<String>(context,
      android.R.layout.simple_spinner_item);
    super.setAdapter(adapter);
  }

  @Override
  public void onClick(DialogInterface dialog, int which, boolean isChecked) {
    if (selection != null && which < selection.length) {
      selection[which] = isChecked;
      updateAdapter();
      getOnItemSelectedListener().onItemSelected(MultiSelectionSpinner.this, MultiSelectionSpinner.this, 0, -1);
    } else {
      throw new IllegalArgumentException(
        "Argument 'index' is out of bounds.");
    }
  }

  private void updateAdapter() {
    adapter.clear();
    adapter.add(buildSelectedItemString());
    super.setSelection(0);
  }

  private String buildSelectedItemString() {
    StringBuilder sb = new StringBuilder();
    boolean foundOne = false;

    for (int i = 0; i < names.length; ++i) {
      if (selection[i]) {
        if (foundOne) {
          sb.append(", ");
        }
        foundOne = true;

        sb.append(names[i]);
      }
    }
    if (!foundOne) {
      return String.valueOf( getPrompt());
    }
    return sb.toString();
  }

  @Override
  public void setAdapter(SpinnerAdapter adapter) {
    throw new RuntimeException(
      "setAdapter is not supported by MultiSelectSpinner.");
  }

  @Override
  public boolean performClick() {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setMultiChoiceItems(names, selection, this);
    builder.setCancelable(true);
//    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//      @Override
//      public void onCancel(DialogInterface dialog) {
//      }
//    });
    builder.show();
    return true;
  }

  public void setItems(String[] items) {
    names = items;
    selection = new boolean[names.length];
    adapter.clear();
    Arrays.fill(selection, false);
  }

  public void setItems(List items) {
//    this.items = items;
    names = new String[items.size()];
    for (int i = 0; i < names.length; ++i) {
      names[i] = items.get(i).toString();
    }
    selection = new boolean[names.length];
    updateAdapter();
    Arrays.fill(selection, false);
  }

  public void setSelection(String[] selection) {
    for (String cell : selection) {
      for (int j = 0; j < names.length; ++j) {
        if (names[j].equals(cell)) {
          this.selection[j] = true;
        }
      }
    }
  }

  public void setSelection(List<String> selection) {
    for (int i = 0; i < this.selection.length; i++) {
      this.selection[i] = false;
    }
    for (String sel : selection) {
      for (int j = 0; j < names.length; ++j) {
        if (names[j].equals(sel)) {
          this.selection[j] = true;
        }
      }
    }
    updateAdapter();
  }

  @Override
  public void setSelection(int index) {
    for (int i = 0; i < selection.length; i++) {
      selection[i] = false;
    }
    if (index >= 0 && index < selection.length) {
      selection[index] = true;
    } else {
      throw new IllegalArgumentException("Index " + index
        + " is out of bounds.");
    }
    updateAdapter();
  }

  public void setSelection(int[] selectedIndices) {
    for (int i = 0; i < selection.length; i++) {
      selection[i] = false;
    }
    for (int index : selectedIndices) {
      if (index >= 0 && index < selection.length) {
        selection[index] = true;
      } else {
        throw new IllegalArgumentException("Index " + index
          + " is out of bounds.");
      }
    }
    updateAdapter();
  }

  public List<String> getSelectedStrings() {
    List<String> selection = new LinkedList<String>();
    for (int i = 0; i < names.length; ++i) {
      if (this.selection[i]) {
        selection.add(names[i]);
      }
    }
    return selection;
  }

  public List<Integer> getSelectedIndices() {
    List<Integer> selection = new LinkedList<Integer>();
    for (int i = 0; i < names.length; ++i) {
      if (this.selection[i]) {
        selection.add(i);
      }
    }
    return selection;
  }
//
//  public List<T> getSelectedItems() {
//    List<T> selection = new LinkedList<T>();
//    for (int i = 0; i < names.length; ++i) {
//      if (selection[i]) {
//        selection.add(items.get(i));
//      }
//      Log.d("selected offers", String.valueOf(i + " : " + selection[i]));
//    }
//    return selection;
//  }

  interface MultiSelectionAdapter {

  }

}
