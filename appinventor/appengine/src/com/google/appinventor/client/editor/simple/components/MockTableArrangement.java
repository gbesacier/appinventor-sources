// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.client.editor.simple.components;

import com.google.appinventor.client.editor.simple.SimpleEditor;
import com.google.appinventor.components.common.ComponentConstants;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * Mock TableArrangement component.
 *
 * @author lizlooney@google.com (Liz Looney)
 */
public final class MockTableArrangement extends MockContainer {

  /**
   * Component type name.
   */
  public static final String TYPE = "TableArrangement";

  // Property names
  private static final String PROPERTY_NAME_COLUMNS = "Columns";
  private static final String PROPERTY_NAME_ROWS = "Rows";
  private static final String PROPERTY_NAME_STRETCHABLE_COLUMNS = "StretchableColumns";
  private static final String PROPERTY_NAME_SHRINKABLE_COLUMNS = "ShrinkableColumns";

  // Form UI components
  protected final AbsolutePanel layoutWidget;

  /**
   * Creates a new MockTableArrangement component.
   *
   * @param editor  editor of source file the component belongs to
   */
  public MockTableArrangement(SimpleEditor editor) {
    super(editor, TYPE, images.table(), new MockTableLayout());

    rootPanel.setHeight("100%");

    layoutWidget = new AbsolutePanel();
    layoutWidget.setStylePrimaryName("ode-SimpleMockContainer");
    layoutWidget.add(rootPanel);

    initComponent(layoutWidget);
  }

  public void removeComponent(MockComponent component, boolean permanentlyDeleted) {
    component.changeProperty(MockVisibleComponent.PROPERTY_NAME_ROW,
        "" + ComponentConstants.DEFAULT_ROW_COLUMN);
    component.changeProperty(MockVisibleComponent.PROPERTY_NAME_COLUMN,
        "" + ComponentConstants.DEFAULT_ROW_COLUMN);
    super.removeComponent(component, permanentlyDeleted);
  }

  private void setColumnsProperty(String value) {
    try {
      int columns = Integer.parseInt(value);
      ((MockTableLayout) layout).setColumns(columns);
    } catch (NumberFormatException e) {
      // Ignore this. If we throw an exception here, the project is unrecoverable.
    }
  }

  private void setRowsProperty(String value) {
    try {
      int rows = Integer.parseInt(value);
      ((MockTableLayout) layout).setRows(rows);
    } catch (NumberFormatException e) {
      // Ignore this. If we throw an exception here, the project is unrecoverable.
    }
  }

  private void setStretchableColumnsProperty(String value) {
    ((MockTableLayout) layout).setStretchableColumns(value);
  }

  private void setShrinkableColumnsProperty(String value) {
    ((MockTableLayout) layout).setShrinkableColumns(value);
  }

  @Override
  public void onPropertyChange(String propertyName, String newValue) {
    super.onPropertyChange(propertyName, newValue);

    // Apply changed properties to the mock component
    if (propertyName.equals(PROPERTY_NAME_COLUMNS)) {
      setColumnsProperty(newValue);
    } else if (propertyName.equals(PROPERTY_NAME_ROWS)) {
      setRowsProperty(newValue);
    } else if (propertyName.equals(PROPERTY_NAME_STRETCHABLE_COLUMNS)) {
      setStretchableColumnsProperty(newValue);
      refreshForm();
    } else if (propertyName.equals(PROPERTY_NAME_SHRINKABLE_COLUMNS)) {
      setShrinkableColumnsProperty(newValue);
      refreshForm();
    }
  }
}
