// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components

import java.lang.reflect.Field

fun Field.hasAnnotation(annotation: Annotation) = annotations.any { it == annotation }