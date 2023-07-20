implicit import ideal.views;

-- Item state
datatype todo_item {
  string name;
  boolean completed;
}

-- List state
datatype todo_list {
  string title;
  list[todo_item] items;
}

todo_list initial_list() => todo_list.new("Todo Manager", []);

void main() {
  run_application(build_todo_application(initial_list()));
}

application_view build_todo_application(the todo_list) {
  return application_view.new(
    title: the_todo_list.title,
    style: application_style,
    home: build_todo_body(the_todo_list)
  );
}

view build_todo_body(the todo_list) {
  -- item_views_list: the_todo_list.items.map(
  --       (the todo_litem) => build_item_view(the_todo_item, the_todo_list)),
  return scaffold_view.new(
    application_bar: label_view.new(the_todo_list.title),
    body: list_view.new(
      children: the_todo_list.items.map(
          (the todo_litem) => build_item_view(the_todo_item, the_todo_list)),
      style: body_style
    ),
    floating_action_button: floating_action_button_view.new(
      icon: Icons.add,
      on_pressed: () => display_add_dialog(the_todo_list),
      tooltip: "Add a Todo"
    ),
  );
}

view build_item_view(the todo_item, the todo_list) {
  void completed_state_changed() => the_todo_item.completed = !the_todo_item.completed;
  void remove_item() => the_todo_list.items.remove(the_todo_item);

  reference[style or null] item_style(boolean completed) {
    return completed ? completed_style : missing;
  }

  return list_tile.new(
    on_tap: completed_state_changed,
    leading: checkbox_view.new(
      value: the_todo_item.completed,
      on_changed: completed_state_changed,
      style: checkbox_style
    ),
    title: row.new(children: [
      expanded.new(
        child: label_view.new(the_todo_item.name,
            style: item_style(the_todo_item.completed))
      ),
      icon_button.new(style: remove_icon_style, on_pressed: remove_item)
    ])
  );
}

void display_add_dialog(the todo_list) {
  var new_item_name = "";
  void exit_dialog(boolean success) {
    if (success) {
      the_todo_list.items.add(todo_item.new(name: new_item_name, completed: false));
    }
  }

  show_dialog(alert_dialog.new(
    title: label_view.new("Add a todo"),
    style: add_dialog_style,
    on_exit: exit_dialog,
    content: text_editor.new(
      text: new_item_name,
      decoration: input_decoration.new(hintText: "Type your todo"),
      autofocus: true
    ),
    cancel_button: button_view.new(
      child: label_view.new("Cancel"),
      style: cancel_button_style
    ),
    ok_button: button_view.new(
      child: label_view.new("Add"),
      style: add_button_style
    ),
  );
}

-- Styling information.  Supplied by the UX designer.
application_style : ThemeData.new(primarySwatch: Colors.blue);
body_style : Padding.new(EdgeInsets.symmetric(vertical: 8.0));
completed_style : TextStyle.new(color: Colors.black54, decoration: TextDecoration.lineThrough);
checkbox_style : CheckboxStyle.new(checkColor: Colors.greenAccent, activeColor: Colors.red);
remove_icon_style: IconButtonStyle.new(iconSize: 30, icon: Icons.delete,
    color: Colors.red, alignment: Alignment.centerRight);
add_dialog_style: AlertDialogStyle.new(barrierDismissible: true);
cancel_button_style: OutlinedButton.new(shape: RoundedRectangleBorder(
    borderRadius: BorderRadius.circular(12)));
add_button_style: ElevatedButton.new(shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12)));
