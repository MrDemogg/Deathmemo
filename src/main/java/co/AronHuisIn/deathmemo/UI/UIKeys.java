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
        public static final String DIMENSION_NAMESPACE = "dimension-namespace";
        public static final String DIMENSION_NAME = "dimension-name";
        public static final String INFO_CONTAINER = "info-container";
        public static final String SNAPSHOT_WINDOW = "snapshot-window";
        public static final String POS_BTN = "posBtn";
        public static final String XP_BTN = "xpBtn";
        public static final String ITEMS_CONTAINER = "items-container";

        public static final class Templates {
            private Templates() {}

            public static final class Pos {
                private Pos() {}

                public static final String TEMPLATE_NAME = "pos";
                public static final String POS_X = "posX";
                public static final String POS_Y = "posY";
                public static final String POS_Z = "posZ";
            }
            public static final class SnapshotContainer {
                private SnapshotContainer() {}

                public static final String TEMPLATE_NAME = "snapshot-container";
            }
            public static final class ItemSlot {
                private ItemSlot() {}

                public static final String TEMPLATE_NAME = "itemSlot";
                public static final String ITEM = "item";
                public static final String HOVERED_ITEM = "hovered-item";
            }

            public static final class FlatButton
            {
                private FlatButton() {}

                public static final String TEMPLATE_NAME = "flatButton";
                public static final String BUTTON_TEXT_ATTR = "button-text";
                public static final String BUTTON_ID_ATTR = "button-id";
                public static final String BUTTON_TEXT = "button-text";
            }
        }
    }
}
