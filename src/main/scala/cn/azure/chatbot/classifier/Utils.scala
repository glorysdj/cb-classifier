/*
 * Copyright 2016 The BigDL Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.azure.chatbot.classifier

import com.intel.analytics.bigdl.tensor.Tensor

object Utils {
  /**
   * Concat tensors. Require tensors have same shape.
   * Say input tensor number is N and shape is a * b * c, the return tensor is
   * N * a * b * c
   * @param tensors input tensors
   * @param buffer reusable buffer to avoid frequent GC. If it's null, we will
   *               allocate a new piece of memory to hold the result
   */
  def concat(tensors: Seq[Tensor[Float]], buffer: Tensor[Float] = null)
  : Tensor[Float] = {
    val _buffer = if (buffer == null) Tensor[Float]() else buffer
    if (tensors == null || tensors.isEmpty) {
      // Do nothing, maybe throw an warning here
    } else if(tensors.length == 1) {
      _buffer.resizeAs(tensors.head).copy(tensors.head)
    } else {
      val head = tensors.head
      tensors.foreach(t =>
        require(head.isSameSizeAs(t), "input tensor have different shapes"))
      _buffer.resize(Array(tensors.length) ++ head.size())
      var d = 1
      while(d <= tensors.length) {
        _buffer.select(1, d).copy(tensors(d - 1))
        d += 1
      }
    }
    _buffer
  }

  /**
   * Concat arrays. Require arrays have same length.
   * Say input array number is N and length is d, the return tensor is
   * N * d
   * @param arrays input arrays
   * @param buffer reusable buffer to avoid frequent GC. If it's null, we will
   *               allocate a new piece of memory to hold the result
   */
  def concatArray(arrays: Seq[Array[Float]], buffer: Tensor[Float] = null)
  : Tensor[Float] = {
    val _buffer = if (buffer == null) Tensor[Float]() else buffer
    if (arrays == null || arrays.isEmpty) {
      // Do nothing, maybe throw an warning here
    } else if(arrays.length == 1) {
      val size = arrays.head.length
      _buffer.resize(size)
      System.arraycopy(arrays.head, 0, _buffer.storage().array(), 0, size)
    } else {
      val size = arrays.head.length
      arrays.foreach(arr =>
        require(size == arr.length, "input array have different lengths"))
      _buffer.resize(Array(arrays.length, size))
      var d = 0
      while(d < arrays.length) {
        System.arraycopy(arrays(d), 0, _buffer.storage().array(), d * size, size)
        d += 1
      }
    }
    _buffer
  }
}
