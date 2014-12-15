package im.delight.android.keyvaluespinner;

/**
 * Copyright 2014 www.delight.im <info@delight.im>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * Spinner component that works with keys of an arbitrary class and exposes both the selected key and value with <code>spinner.getKey();</code> and <code>spinner.getValue();</code>
 *
 * @param <K> the class for each entry's key
 */
public class KeyValueSpinner<K> extends Spinner {

	private Pair<K> mSelected;
	private AdapterView.OnItemSelectedListener mAdditionalOnItemSelectedListener;
	private final AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			Object selected = parent.getItemAtPosition(position);
			if (selected instanceof Pair<?>) {
				mSelected = (Pair<K>) selected;
				if (mAdditionalOnItemSelectedListener != null) {
					mAdditionalOnItemSelectedListener.onItemSelected(parent, view, position, id);
				}
			}
			else {
				throw new RuntimeException("A value has been selected that is not of class KeyValuePair<K>");
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			if (mAdditionalOnItemSelectedListener != null) {
				mAdditionalOnItemSelectedListener.onNothingSelected(parent);
			}
		}

	};

	public KeyValueSpinner(Context context) {
		super(context);
		init();
	}

	public KeyValueSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public KeyValueSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/** Must be called manually if you use the constructor <code>Spinner(Context context, AttributeSet attrs, int defStyle, int mode)</code>, will be called automatically, otherwise */
	public void init() {
		super.setOnItemSelectedListener(mOnItemSelectedListener);
	}

	@Override
	public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
		mAdditionalOnItemSelectedListener = listener;
	}

	public K getKey() {
		if (mSelected == null) {
			return null;
		}
		else {
			return mSelected.getKey();
		}
	}

	public CharSequence getValue() {
		if (mSelected == null) {
			return null;
		}
		else {
			return mSelected.getValue();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setAdapter(SpinnerAdapter adapter) {
		if (adapter instanceof Adapter) {
			super.setAdapter(adapter);

			try {
				Object first = adapter.getItem(0);
				if (first instanceof Pair<?>) {
					mSelected = (Pair<K>) first;
				}
			}
			catch (Exception e) { }
		}
		else {
			throw new RuntimeException("You must pass an instance of KeyValueSpinnerAdapter to setAdapter(...)");
		}
	}

	@SuppressWarnings("unchecked")
	public void sort() {
		final SpinnerAdapter adapter = getAdapter();
		if (adapter != null) {
			if (adapter instanceof Adapter) {
				((Adapter<K>) adapter).sort();
			}
		}
		else {
			throw new RuntimeException("You cannot sort the Spinner's values when there is no Adapter set yet");
		}
	}

	/**
	 * Adapter that accepts instances of KeyValuePair and may be used as the data source for a KeyValueSpinner.
	 *
	 * @param <K> the class for the KeyValuePairs' keys
	 */
	public static class Adapter<K> extends ArrayAdapter<Pair<K>> implements SpinnerAdapter {

		public Adapter(Context context, int resource) {
			super(context, resource);
			init();
		}

		public Adapter(Context context, int resource, int textViewResourceId) {
			super(context, resource, textViewResourceId);
			init();
		}

		public Adapter(Context context, int resource, int textViewResourceId, Pair<K>[] objects) {
			super(context, resource, textViewResourceId, objects);
			init();
		}

		public Adapter(Context context, int resource, int textViewResourceId, List<Pair<K>> objects) {
			super(context, resource, textViewResourceId, objects);
			init();
		}

		public Adapter(Context context, int textViewResourceId, Pair<K>[] objects) {
			super(context, textViewResourceId, objects);
			init();
		}

		public Adapter(Context context, int textViewResourceId, List<Pair<K>> objects) {
			super(context, textViewResourceId, objects);
			init();
		}

		protected void init() {
			super.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		}

		/**
		 * Creates a new KeyValueSpinner.Adapter from external resources
		 *
		 * @param context the application context for access to the Resources and initializing the adapter
		 * @param keysArrayResId the string-array resource holding the keys
		 * @param valuesArrayResId the string-array resource holding the values
		 * @param textViewResId the identifier for the view layout
		 * @return the new Adapter
		 */
		public static Adapter<CharSequence> createFromResource(Context context, int keysArrayResId, int valuesArrayResId, int textViewResId) {
			final Resources resources = context.getResources();

			final CharSequence[] keys = resources.getTextArray(keysArrayResId);
			final CharSequence[] values = resources.getTextArray(valuesArrayResId);

			return createFromArrays(context, keys, values, textViewResId);
		}

		/**
		 * Creates a new KeyValueSpinner.Adapter from two CharSequence[] arrays
		 *
		 * @param context the application context for initializing the adapter
		 * @param keys the array holding the keys
		 * @param values the array holding the values
		 * @param textViewResId the identifier for the view layout
		 * @return the new Adapter
		 */
		public static Adapter<CharSequence> createFromArrays(Context context, CharSequence[] keys, CharSequence[] values, int textViewResId) {
			final List<Pair<CharSequence>> entries = new LinkedList<Pair<CharSequence>>();

			for (int k = 0; k < keys.length; k++) {
				entries.add(new KeyValueSpinner.Pair<CharSequence>(keys[k], values[k]));
			}

			return new Adapter<CharSequence>(context, textViewResId, entries);
		}

		public void sort() {
			super.sort(new PairComparator<K>());
		}

	}

	/**
	 * Wrapper class for key-value pairs. Keys may be of any class and values must be of CharSequence or any subclass.
	 *
	 * @param <K> the class for the keys
	 */
	public static class Pair<K> {

		public final K mKey;
		public final CharSequence mValue;

		public Pair(K key, CharSequence value) {
			mKey = key;
			mValue = value;
		}

		public K getKey() {
			return mKey;
		}

		public CharSequence getValue() {
			return mValue;
		}

		@Override
		public String toString() {
			if (mValue == null) {
				return null;
			}
			else {
				return mValue.toString();
			}
		}

	}

	public static class PairComparator<K> implements Comparator<Pair<K>> {

		@Override
		public int compare(Pair<K> a, Pair<K> b) {
			return a.getValue().toString().compareToIgnoreCase(b.getValue().toString());
		}

	}

}
