package com.lizzaraga.ziker.domains.models

enum class FileDropdownAction(val title: String){
    Select(title = "Select"),
    SelectAll(title = "Select all"),
    DeselectAll(title = "Deselect all"),
    OrderBy(title = "Order by"),
    Favorites(title = "Add to favorites"),
    Rename("Rename"),
    Share("Share"),
    MoveToTrash("Move to trash"),
    FileInfo("File info"),
    OpenWith("Open with"),
}