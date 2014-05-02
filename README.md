# Android-KeyValueSpinner

Spinner component that works with normal values but additionally lets you use keys of an arbitrary class.

The selected key is exposed via `spinner.getKey();` and the selected value via `spinner.getValue();`.

## Installation

 * Copy the Java package to your project's source folder
 * or
 * Create a new library project from this repository and reference it in your project

## Usage

### Layout (XML)

```
<im.delight.android.keyvaluespinner.KeyValueSpinner
	android:id="@+id/planets_spinner"
	android:layout_width="fill_parent"
	android:layout_height="wrap_content" />
```

### Activity (Java)

```
KeyValueSpinner<CharSequence> spinner = (KeyValueSpinner<CharSequence>) findViewById(R.id.spinner);
// Create a KeyValueSpinner.Adapter using two string arrays and a default spinner layout
KeyValueSpinner.Adapter<CharSequence> adapter = KeyValueSpinner.Adapter.createFromResource(this, R.array.planets_keys, R.array.planets_values, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
spinner.setAdapter(adapter);
```

## Creating the adapter from Java arrays

```
CharSequence[] keys;
CharSequence[] values;
...
KeyValueSpinner.Adapter<CharSequence> adapter = KeyValueSpinner.Adapter.createFromArrays(this, keys, values, android.R.layout.simple_spinner_item);
```

## Using the constructor with four parameters

If you want to use the new constructor `Spinner(Context context, AttributeSet attrs, int defStyle, int mode)`, which is available for API levels 11+ only, you have to call `spinner.init();` manually. Otherwise, this is called automatically.

## License

```
Copyright 2014 www.delight.im <info@delight.im>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```