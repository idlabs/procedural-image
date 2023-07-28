Copyright © Inter IKEA Systems B.V. 2021

# Operator List - Procedural Material

## Introduction

The purpose of `procedural image` is to provide a framework that enables generation of procedurally generated images.  
One goal is to be able to use these images as texture sources in 3D content (computer generated images), both as a source for very high quality production (raytraced) images and also as a source for realtime 3D model textures.  
It shall be possible to generate source images on target devices in runtime without prohibitive time consumption.  
Times for generation of images shall typically be in the region of the time it takes to decode png/jpeg images, obviously this depends on the complexity of the procedural recipe.    

The core of `procedural image` is made up by the operator list.  
This list is the set of commands or operators that make up a recipe for generating source images.  

The operator list, recipe, is a set of operators that are executed in sequence, each having one or more inputs and one output.  

Input and output is made up of 4 component vectors, where the fourth component is the magnitude.  


## Generation

Generation of a procedural image from a recipe is done by executing the operators in sequence, the last produced value is stored in the output buffer.  

This is the output from a blend operator, blending two inputs - foreground and background.  
As you can see this is not much of a procedural image - simply one color  

| Blend operator | Position input to blend |
:-------:|:------:
![image](https://github.com/idlabs/procedural-image/assets/3063192/80dbc157-0592-4b20-851c-c3b272350285) | ![image](https://github.com/idlabs/procedural-image/assets/3063192/fb722064-b611-4fc6-b616-263a8681eae0)


With position input to blend there are different colored pixels in the image - much more interesting than a one colored image :-)    
This is to show that using (world) position as input to operators is one way of generating a changing output.  

**World position**

The world position will change based on which pixel (x, y) that is generated.  
By default the world position will go from [-1.0,-1.0] to [1.0, 1.0]  
These values are chosen to keep position values in a range where precision is high, 7 decimals when using single precision floating point (32 bit floats)  
Many operators do not 'scale' well when using integers, if needed there is an option to change the world position in the asset.  
For instance making the world position go from [-20.0, -20.0] to [20.0, 20.0] if wanted.  

| Voronoi, position input | Voronoi, scaled position as input |
:-------:|:------:
![image](https://github.com/idlabs/procedural-image/assets/3063192/29fe5796-23cb-46da-afd9-a82a8ebd25ab) | ![image](https://github.com/idlabs/procedural-image/assets/3063192/a156b780-c987-4d3b-ac76-dd3b89da6408)




## Syntax

OLL is based on JSON with two top level objects, _**operators**_ and _**outputs**_.

```json

    "operators": {},
    "outputs": {}

```

where 
- *`operators`* is a list of key-value pairs, where the key is the name and the value is the operator    
- *`outputs`* is a set of names of the operators the produce the output, one each for RGB, ROUGHNESS and NORMAL MAP  


### Operator Object

An operator object consists of properties `operator` and `parameters` and is uniquely defined by its key in the `operator` object. 

```json 
    "operator": "position",
    "parameters": {}

```

Where `parameters` is a list of key-value pairs.  
Each parameter is defined by its key in the `parameters` object and contains a value of type `int`, `float`, or `float[]`.

```json
    "parameters": {
        "factor_1": [ 0.0, 1.0, 0.0, 0.0 ],
        "factor_2": [ 1.0, 2.0, 0.0, 0.0 ]
    }

```

A parameter can reference another operation as well. In this case, the output of the referenced operation is used as parameter value:
```json

    "factor_2": {
          "$ref": "#/operations/n11"
        }

```


### Output Object

An output object consists of a list of key-value pairs.  
The key defines the `output name` and the value references the operation that should be the last to contribute to the output. 

```json
  "outputs": {
    "rgb": {
      "$ref": "#/operations/n1"
    },
    "normal": {
      "$ref": "#/operations/n6"
    },
  }
```

# Operators

## Generator Operators

These operators can be seen as the base of procedurally generated images, they are the operators that produce an output based on input value.  

### Voronoi

Operator name: `voronoi`

**parameters**
- `input` vec3
- `randomness` float

Output:
A vec4 containing the cell center position in the first three components, in the range [-1.0 - 1.0], and the  voronoi distance value in the last component.  


**Example**

Below examples are generated with the same randomness, showing the effects of transforming the input position.  
The asset specifies world coordinates from -1.0 to 1.0  

| Generation from position | Offset position | Scaled position | Rotated (45°) position |
:-----:|:-----:|:-----:|:-----:
![image](https://github.com/idlabs/procedural-image/assets/3063192/9a11e8f2-40d0-421a-b475-22b913286875) | ![image](https://github.com/idlabs/procedural-image/assets/3063192/2c706cdf-51d1-4ce0-9136-00c0c206c9a0) | ![image](https://github.com/idlabs/procedural-image/assets/3063192/9e45bae8-977c-40db-af49-42f780412be1) | ![image](https://github.com/idlabs/procedural-image/assets/3063192/1afc312a-304e-42c8-bbed-7e404ad7c257)


### Noise
Operator name:`noise`

**Parameters**
- `input` vec3
- `min_level` int
- `max_level` int
- `beta` float

Creates a brownian noise that consists of multiple octaves of noise laid over each other with a signal intensity falloff for lower amplitudes.

`min_level` and `max_level` specify the range of fractal levels. You can limit the noise to a single level by setting the `min_level` and `max_level` to the same value, which will result in white noise.

`beta` is used to specify the [color of the noise](https://en.wikipedia.org/wiki/Colors_of_noise).

**Example**

Below examples are generated with min_level = 0, max_level = 1, beta = 0.0  
This is to show the effects of transforming the input position.  
The asset specifies world coordinates from -1.0 to 1.0  

| Generation from position | Offset position | Scaled position | Rotated (45°) position |
:-----:|:-----:|:-----:|:-----:
![image](https://github.com/idlabs/procedural-image/assets/3063192/9cca4c28-d5a7-4963-8460-dea06f5a61f8) | ![image](https://github.com/idlabs/procedural-image/assets/3063192/caf31e14-1413-4dc0-8bbe-2d015d3ebe68) | ![image](https://github.com/idlabs/procedural-image/assets/3063192/ed9fd21a-e367-4d9f-bac4-5c03173f36c5) | ![image](https://github.com/idlabs/procedural-image/assets/3063192/f613806c-cf58-46c9-96cd-1af102099863)



### Curve
Operator name:`curve`

**parameters**
- `red` float[] *two components per control point: (position, luminosity)*
- `green` float[] *two components per control point: (position, luminosity)*
- `blue` float[] *two components per control point: (position, luminosity)*
- `alpha` float[] *two components per control point: (position, alpha value)*
- `input` vec4

This node allows to precisely map input color values to output values. The transfer function is given by a set of bezier curves.

Each channel `red`, `green` and `blue`'s bezier curve is specified by an array of control points where four control points form a bezier segment. 


**Example**

Below examples are generated with an asset that specifies world coordinates from 0.0 - 1.0 (this is why the curve axis of rotation is not in center of screen)    

| Generation from position | Offset position | Scaled position | Rotated (45°) position |
:-----:|:-----:|:-----:|:-----:
![image](https://github.com/idlabs/procedural-image/assets/3063192/e2146074-6b86-4cc1-8ada-097906748a57) | ![image](https://github.com/idlabs/procedural-image/assets/3063192/9b7f1ca5-94fd-454d-bd80-5ac8764d6e95) | ![image](https://github.com/idlabs/procedural-image/assets/3063192/b4e35906-9661-41aa-ab4d-e5261f5ca5f4) | ![image](https://github.com/idlabs/procedural-image/assets/3063192/ae52a435-75e8-4609-b242-84cc31f430ba)







**Blend**
Operator Name: `blend`

**Parameters**
- `opacity` float
- `foreground` vec4
- `background` vec4
- `blend_mode` int

x := foreground, y := background, r := blending result, f := opacity.

| `blend_mode`  | Name          | Formula                           |
| ---           | ---           | ---                               |
| 0             | Normal        | `r = x`                           |
| 1             | Linear Dodge  | `r = x + y`                       |
| 2             | Subtract      | `r = y - x`                       |
| 3             | Multiply      | `r = x * y`                       |
| 4             | Divide        | `r = y / x)`                      |
| 5             | Screen        | `r = 1 - (1 - x) * (1 - y)`       |
| 6             | Max           | `r = { max(x.x, y.x), max(x.y, y.y), max(x.z, y.z)}` |
| 7             | Min           | `r = { min(x.x, y.x), min(x.y, y.y), min(x.z, y.z)}` |


All blend operations blend the result `r` together with the background layer using `final pixel = clamp(f * r + (1 - f) * y, -1.0, 1.0)`.  

**Example**


### Position

Operator name: `position`

Output contains the normalized world position of the fragment in the range [-1.0 - 1.0]  

**Example**






### Sawtooth Wave

Operator name: `sawtooth_wave`

**parameters**
- `input` vec3

Applies a sawtooth wave function to each channel of the input vector.

**Example**


### Linear To Srgb

Operator name: `linear_to_srgb`

**parameters**
- `input` vec4

Converts [linear color inputs to sRGB](https://en.wikipedia.org/wiki/SRGB#From_CIE_XYZ_to_sRGB)

### Srgb To Linear

Operator name:`srgb_to_linear`

**parameters**
- `input` vec4

Converts [sRGB color inputs to linear](https://en.wikipedia.org/wiki/SRGB#From_sRGB_to_CIE_XYZ)


### Colorize
Operator name:`colorize`

**Parameters**
- `keys` float[] *five components per key: (luminosity, r, g, b, a)*
- `input` float


Maps a grayscale input to a color output by linear mappings from grayscale to color. 

The keys must be sorted by luminosity. 

There have to be at least two keys. 

If the first key starts at a luminosity value higher that zero, all luminosity values before it map to the color (r, g, b, a) value of the first key, likewise for the last key and luminosities higher than the last keys luminosity.

**Example**


### Grayscale Conversion
Operator name:`grayscale`

**Parameters**
- `input` vec4
- `weights` vec4

Converts the input image to grayscale. 

```grayscale = (l_r * w_r + l_g * w_g + l_b * w_b + l_a * w_a ) / (w_r + w_g + w_b + w_a)```

where `l_*` denotes the luminosity value from the input per channel and `w_*` the weight per input channel.

You can ignore the alpha channel by setting the weights to `(1, 1, 1, 0)`.

**Example**


### HSL

Operator name:`hsl`

**Parameters**
- `hue` float
- `saturation` float
- `lightness` float
- `input` vec4


Allows adjustements of Hue, Saturation and Lightness to the input texture. First this note converts to HSL color space, then it applies the color correction, and finally it converts back to rgb color space. The default value for the Hue, Saturation, and Lightness inputs is 0.5. All values above that increase the respecive value, all below decrease it.

**Example**


### Levels
Operator name:`levels`

**Parameters**
- `black_point` vec4
- `gamma` vec4
- `white_point` vec4
- `output_from` vec4
- `output_to` vec4
- `input` vec4

This node allows to remap the tones in a texture by setting the input and output remap factors.

**Example**


### Uniform Color
Operator name:`uniform_color`

**Parameters**
- `output_color` vec4

Outputs a vec4 value  

**Example**

### UVW Coordinate

Operator name:`uvw_coordinate`

Maps texture UVW coordinates ([0,1]x[0,1]x[0,1]) to the output.

**Example**

### Blur

Operator name:`blur`

**Parameters**
- `intensity` float
- `input` operation

Performs a gaussian blur over the input image. The `intensity` parameter corresponds to the standard deviation of the gaussian blur.

**Example**


### Normal Map
Operator name:`normal_map`


**Parameters**
- `intensity` float
- `input` operation

Compute the tangent space normal map

**Example**

### Sharpen
Operator name:`sharpen`

**Parameters**
- `intensity` float
- `input` operation

Performs a sharpening operation on the input image.

**Parameters**


### Transform
Operation name:`transform`

**Parameters**
- `translate` vec4
- `rotate` vec4
- `scale` vec4
- `input` operation

Allows to apply these transformations to the input : scale, rotate and proportion changes. (Applies a transformation matrix to the input).

**Example**

### Combine
Operator name:`combine`

**Parameters**
- `red` float
- `green` float
- `blue` float
- `alpha` float

Takes the input values and places them in the respective channels of the output.

**Example**


### Addition
Operator name:`add`

**Parameters**
- `summand_1` vec4
- `summand_2` vec4

```output = summand_1 + summand_2```

**Example**


### Subtraction
Operator name: `subtract`


**Parameters**
- `minuend` vec4
- `subtrahend` vec4

```output = minuend - subtrahend```

**Example**


### Multiplication
Operator name:`multiply`


**Parameters**
- `factor_1` vec4
- `factor_2` vec4

```output = factor_1 * factor_2```

**Example**


### Division
Operator name:`divide`

**Parameters**
- `dividend` vec4
- `divisor` vec4

```output = dividend / divisor```

**Example**


### Matrix Multiplication

Operator name:`matrix_multiply`

**Parameters**
- `matrix` mat4
- `vector` vec4

``` output = matrix * vector```


**Example**

### L2 Norm

Operator name:`l2_norm`

**Parameters**
- `input` vec4

``` output = sqrt(dot(input, input))```

**Example**

## 4. Example



