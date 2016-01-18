package us.petrolog.nexus.misc;

public class IntegerComparator implements java.util.Comparator<XYMerger> {

    boolean ascending;

    public IntegerComparator(boolean isAscending) {
        this.ascending = isAscending;
    }

    @Override
    public int compare(XYMerger obj1 , XYMerger obj2) {
        if (ascending) {
            return (obj1.getX() < obj2.getX() ? -1 : (obj1.getX() == obj2.getX() ? 0 : 1));
        }
        return (obj1.getX() > obj2.getX() ? -1 : (obj1.getX() == obj2.getX() ? 0 : 1));
    }

}