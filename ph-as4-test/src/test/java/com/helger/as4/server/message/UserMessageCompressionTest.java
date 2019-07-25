/**
 * Copyright (C) 2015-2019 Philip Helger (www.helger.com)
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
package com.helger.as4.server.message;

import java.io.InputStream;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Document;

import com.helger.as4.AS4TestConstants;
import com.helger.as4.attachment.EAS4CompressionMode;
import com.helger.as4.attachment.WSS4JAttachment;
import com.helger.as4.crypto.AS4SigningParams;
import com.helger.as4.crypto.ECryptoAlgorithmCrypt;
import com.helger.as4.error.EEbmsError;
import com.helger.as4.http.HttpMimeMessageEntity;
import com.helger.as4.messaging.crypto.AS4Encryptor;
import com.helger.as4.messaging.crypto.AS4Signer;
import com.helger.as4.messaging.domain.AS4UserMessage;
import com.helger.as4.messaging.mime.MimeMessageCreator;
import com.helger.as4.server.external.IHolodeckTests;
import com.helger.as4.soap.ESOAPVersion;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.mime.CMimeType;

@RunWith (Parameterized.class)
public final class UserMessageCompressionTest extends AbstractUserMessageTestSetUp
{
  @Parameters (name = "{index}: {0}")
  public static Collection <Object []> data ()
  {
    return CollectionHelper.newListMapped (ESOAPVersion.values (), x -> new Object [] { x });
  }

  private final ESOAPVersion m_eSOAPVersion;

  public UserMessageCompressionTest (@Nonnull final ESOAPVersion eSOAPVersion)
  {
    m_eSOAPVersion = eSOAPVersion;
  }

  @Category (IHolodeckTests.class)
  @Test
  public void testUserMessageWithCompressedAttachmentSuccessful () throws Exception
  {
    final ICommonsList <WSS4JAttachment> aAttachments = new CommonsArrayList <> ();
    aAttachments.add (WSS4JAttachment.createOutgoingFileAttachment (ClassPathResource.getAsFile (AS4TestConstants.TEST_SOAP_BODY_PAYLOAD_XML),
                                                                    CMimeType.APPLICATION_XML,
                                                                    EAS4CompressionMode.GZIP,
                                                                    s_aResMgr));

    final MimeMessage aMimeMsg = MimeMessageCreator.generateMimeMessage (m_eSOAPVersion,
                                                                         MockMessages.testUserMessageSoapNotSigned (m_eSOAPVersion,
                                                                                                                    null,
                                                                                                                    aAttachments)
                                                                                     .getAsSOAPDocument (),
                                                                         aAttachments);

    sendMimeMessage (new HttpMimeMessageEntity (aMimeMsg), true, null);
  }

  @Test
  public void testUserMessageWithCompressedSignedSuccessful () throws Exception
  {
    final ICommonsList <WSS4JAttachment> aAttachments = new CommonsArrayList <> ();
    aAttachments.add (WSS4JAttachment.createOutgoingFileAttachment (ClassPathResource.getAsFile (AS4TestConstants.TEST_SOAP_BODY_PAYLOAD_XML),
                                                                    CMimeType.APPLICATION_XML,
                                                                    EAS4CompressionMode.GZIP,
                                                                    s_aResMgr));

    final AS4UserMessage aMsg = MockMessages.testUserMessageSoapNotSigned (m_eSOAPVersion, null, aAttachments);
    final Document aDoc = AS4Signer.createSignedMessage (m_aCryptoFactory,
                                                         aMsg.getAsSOAPDocument (),
                                                         m_eSOAPVersion,
                                                         aMsg.getMessagingID (),
                                                         aAttachments,
                                                         s_aResMgr,
                                                         false,
                                                         AS4SigningParams.createDefault ());
    final MimeMessage aMimeMsg = MimeMessageCreator.generateMimeMessage (m_eSOAPVersion, aDoc, aAttachments);

    sendMimeMessage (new HttpMimeMessageEntity (aMimeMsg), true, null);
  }

  @Test
  public void testUserMessageCompressedEncrpytedSuccessful () throws Exception
  {
    final ICommonsList <WSS4JAttachment> aAttachments = new CommonsArrayList <> ();
    aAttachments.add (WSS4JAttachment.createOutgoingFileAttachment (ClassPathResource.getAsFile (AS4TestConstants.TEST_SOAP_BODY_PAYLOAD_XML),
                                                                    CMimeType.APPLICATION_XML,
                                                                    EAS4CompressionMode.GZIP,
                                                                    s_aResMgr));

    final Document aDoc = MockMessages.testUserMessageSoapNotSigned (m_eSOAPVersion, null, aAttachments)
                                      .getAsSOAPDocument ();

    final MimeMessage aMsg = AS4Encryptor.encryptMimeMessage (m_aCryptoFactory,
                                                              m_eSOAPVersion,
                                                              aDoc,
                                                              false,
                                                              aAttachments,
                                                              s_aResMgr,
                                                              ECryptoAlgorithmCrypt.ENCRPYTION_ALGORITHM_DEFAULT,
                                                              m_sEncryptionAlias);
    sendMimeMessage (new HttpMimeMessageEntity (aMsg), true, null);
  }

  @Test
  public void testUserMessageCompressedSignedEncrpytedSuccessful () throws Exception
  {
    final ICommonsList <WSS4JAttachment> aAttachments = new CommonsArrayList <> ();
    aAttachments.add (WSS4JAttachment.createOutgoingFileAttachment (ClassPathResource.getAsFile (AS4TestConstants.TEST_SOAP_BODY_PAYLOAD_XML),
                                                                    CMimeType.APPLICATION_XML,
                                                                    EAS4CompressionMode.GZIP,
                                                                    s_aResMgr));

    final AS4UserMessage aMsg = MockMessages.testUserMessageSoapNotSigned (m_eSOAPVersion, null, aAttachments);
    final Document aDoc = AS4Signer.createSignedMessage (m_aCryptoFactory,
                                                         aMsg.getAsSOAPDocument (),
                                                         m_eSOAPVersion,
                                                         aMsg.getMessagingID (),
                                                         aAttachments,
                                                         s_aResMgr,
                                                         false,
                                                         AS4SigningParams.createDefault ());

    final MimeMessage aMimeMsg = AS4Encryptor.encryptMimeMessage (m_aCryptoFactory,
                                                                  m_eSOAPVersion,
                                                                  aDoc,
                                                                  false,
                                                                  aAttachments,
                                                                  s_aResMgr,
                                                                  ECryptoAlgorithmCrypt.ENCRPYTION_ALGORITHM_DEFAULT,
                                                                  m_sEncryptionAlias);
    sendMimeMessage (new HttpMimeMessageEntity (aMimeMsg), true, null);
  }

  @Test
  public void testUserMessageWithWrongCompressionType () throws Exception
  {
    try (final InputStream aIS = ClassPathResource.getInputStream ("testfiles/WrongCompression.mime"))
    {
      final MimeMessage aMsg = new MimeMessage (null, aIS);

      sendMimeMessage (new HttpMimeMessageEntity (aMsg), false, EEbmsError.EBMS_VALUE_INCONSISTENT.getErrorCode ());
    }
  }
}
