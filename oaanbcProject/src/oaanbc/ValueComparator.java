package oaanbc;

import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author <p> Syed Shariyar Murtaza </p>
 */
public class ValueComparator implements Comparator <Map.Entry<String,Double>>{

        // Note: this comparator imposes orderings that are inconsistent with
        // equals.
        @Override
        public int compare(Map.Entry<String,Double> a, Map.Entry<String,Double> b) {
            int val= b.getValue().compareTo(a.getValue());
            return val!=0? val:1;
          }
    }

