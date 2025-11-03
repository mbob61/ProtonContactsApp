# Automated Test Cases

This document outlines the automated UI tests implemented for the ToDo App, categorized by their respective test suites. Each test includes a short description of the user path it validates.

---

## Test Suites

### Smoke Tests

These tests cover the critical-path functionality to ensure the app is stable.

- **Create Note via FAB**: User creates a new note using the Floating Action Button, saves it, and verifies it appears in the list (implemented by `createNewNoteViaFAB`).
- **Create Note from Empty State**: User creates a new note from the empty state screen and verifies it is saved correctly (implemented by `createNoteViaEmptyStateButton`).
- **View List of Notes**: Verifies that a list of existing notes is correctly displayed when the app starts (implemented by `viewListOfNotes`).
- **Navigate to Note Details**: User taps on a note in the list and is successfully taken to the details screen (implemented by `navigateToNoteDetails`).
- **Delete Note from List Screen**: User long-presses a note, taps the delete icon, confirms, and verifies the note is removed (implemented by `deleteNoteFromListScreen`).
- **Star Note from List Screen**: User stars a note from the list screen and verifies its state is updated (implemented by `starNoteFromListScreen`).
- **Unstar Note from List Screen**: User unstars a note from the list screen and verifies its state is updated (implemented by `unstarNoteFromListScreen`).
- **Share Note from List Screen**: User shares a note from the list screen and verifies the system share sheet appears with the correct content (implemented by `shareNoteFromListScreen`).
- **Edit Existing Note**: User opens an existing note, edits it, saves, and verifies the changes are reflected in the list (implemented by `editExistingNote`).
- **Delete Note from Details Screen**: User opens a note, deletes it from the details screen, and verifies it is removed from the list (implemented by `deleteNoteFromDetailsScreen`).
- **Star Note from Details Screen**: User stars a note from the details screen and verifies the change is saved (implemented by `starNoteFromDetailsScreen`).

### Regression Tests

This suite provides comprehensive coverage of the app's features to prevent regressions.

- **Edit and Discard Changes**: User edits a note, navigates back, and discards the changes, ensuring the note remains unaltered (implemented by `editAndDiscardChanges`).
- **Edit and Cancel Discarding Changes**: User edits a note, navigates back, cancels the discard dialog, and remains on the details screen (implemented by `editAndCancelDiscard`).
- **Navigate Back without Changes**: Verifies that no discard dialog appears when navigating back from an unchanged note (implemented by `verifyNoChangesAllowForBackNavigation`).
- **Verify Discard Dialog on Back Navigation**: Confirms the discard dialog appears when navigating back from a modified note (implemented by `verifyChangesPreventBackNavigation`).
- **Cancel Deletion from Details Screen**: User initiates a delete from the details screen but cancels the action, ensuring the note is not deleted (implemented by `cancelDeleteFromDetailsScreen`).
- **Toggle Star State Multiple Times**: User repeatedly stars and unstars a note to check for state consistency (implemented by `toggleStarMultipleTimes`).
- **Navigate Back from Details Screen**: Ensures the user can successfully navigate back from the details screen to the list screen (implemented by `navigateBackFromDetails`).
- **Verify Empty State UI**: Verifies that the "No notes yet" UI is displayed when no notes exist (implemented by `verifyEmptyState`).
- **Cancel Deletion from List Screen**: User initiates a delete from the list screen but dismisses it, ensuring the note is not deleted (implemented by `cancelDeleteFromListScreen`).
- **Enter Selection Mode**: Confirms that a long press on a note activates selection mode (implemented by `enterSelectionModeViaLongPress`).
- **Exit Selection Mode**: Verifies that the user can exit selection mode and return to the normal list view (implemented by `exitSelectionMode`).
- **Verify Single Note Selection**: Ensures that only one note can be selected at a time in selection mode (implemented by `verifySingleSelection`).
- **Verify Starred Count**: Checks that the starred notes count in the toolbar updates correctly (implemented by `verifyStarredCount`).
- **Share Note with Empty Title**: Verifies a note with no title can be shared correctly (implemented by `shareNoteWithEmptyTitle`).
- **Share Note with Empty Content**: Verifies a note with no content can be shared correctly (implemented by `shareNoteWithEmptyContent`).
- **Verify Note Persistence on App Restart**: Confirms that notes are saved and persist after the app is restarted (implemented by `verifyNotePersistence`).
- **Full Note Lifecycle**: An end-to-end test covering creating, editing, starring, and deleting a note (implemented by `fullNoteLifecycle`).
- **Verify Long Text Truncation**: Verifies that long titles are correctly truncated with an ellipsis in the list view (implemented by `longTextIsTruncated`).
- **Verify Date Formatting**: Ensures the date on a note card is displayed in the correct "MMM dd, yyyy" format (implemented by `dateIsFormattedCorrectly`).

### Negative Tests

These tests validate the app's behavior under error conditions and with invalid input.

- **No Delete Option for New Notes**: Confirms the delete option is not visible when creating a new, unsaved note (implemented by `deleteOptionNotAvailableForUnsavedNote`).
- **Prevent Saving Empty Notes**: Ensures a completely empty note cannot be saved (implemented by `createNoteWithEmptyTitleAndContent`).
- **Prevent Editing a Note to be Empty**: Prevents an existing note from being edited to become completely empty (implemented by `cannotRemoveContentAndTitleFromANote`).
