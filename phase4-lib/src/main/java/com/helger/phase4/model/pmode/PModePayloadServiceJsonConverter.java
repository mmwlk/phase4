/**
 * Copyright (C) 2015-2020 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.phase4.model.pmode;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.json.IJsonObject;
import com.helger.json.JsonObject;
import com.helger.phase4.attachment.EAS4CompressionMode;

/**
 * JSON converter for objects of class {@link PModePayloadService}.
 *
 * @author Philip Helger
 * @since 0.12.0
 */
@Immutable
public final class PModePayloadServiceJsonConverter
{
  private static final String COMPRESSION_MODE = "CompressionMode";

  private PModePayloadServiceJsonConverter ()
  {}

  @Nonnull
  public static IJsonObject convertToJson (@Nonnull final PModePayloadService aValue)
  {
    final IJsonObject ret = new JsonObject ();
    if (aValue.hasCompressionMode ())
      ret.add (COMPRESSION_MODE, aValue.getCompressionModeID ());
    return ret;
  }

  @Nonnull
  public static PModePayloadService convertToNative (@Nonnull final IJsonObject aElement)
  {
    final String sCompressionModeID = aElement.getAsString (COMPRESSION_MODE);
    final EAS4CompressionMode eCompressionMode = EAS4CompressionMode.getFromIDOrNull (sCompressionModeID);
    if (sCompressionModeID != null && eCompressionMode == null)
      throw new IllegalStateException ("Invalid compression mode ID '" + sCompressionModeID + "' provided!");

    return new PModePayloadService (eCompressionMode);
  }
}