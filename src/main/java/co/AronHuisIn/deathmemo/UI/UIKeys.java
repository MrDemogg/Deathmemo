package co.AronHuisIn.deathmemo.UI;

public final class UIKeys {
    private UIKeys() {}

    public static final class SnapshotsHistory {
        private SnapshotsHistory() {}

        public static final String SCREEN_ID = "snapshots_screen";
        public static final String DATE_TIMES = "dateTimes";
        public static final String ARMOR = "armor";
        public static final String OFFHAND = "offhand";
        public static final String ITEMS = "items";
        public static final String CLOSE_BTN = "closeBtn";
        public static final String SCROLL_EMPTY = "scroll-empty";

        public static final String POS_X = "posX";
        public static final String POS_Y = "posY";
        public static final String POS_Z = "posZ";

        public static final class Examples {
            private Examples() {}

            public static final class ItemSlot {
                private ItemSlot() {}

                public static final String EXAMPLE_NAME = "itemSlot";
                public static final String ITEM = "item";
                public static final String HOVERED_ITEM = "hovered-item";
            }

            public static final class FlatButton
            {
                private FlatButton() {}

                public static final String EXAMPLE_NAME = "flatButton";
                public static final String BUTTON_TEXT_ATTR = "button-text";
                public static final String BUTTON_TEXT = "button-text";
            }
        }
    }
}
