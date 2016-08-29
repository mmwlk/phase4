package com.helger.as4server.message;

import javax.annotation.Nonnull;
import javax.mail.internet.MimeMessage;

import org.apache.wss4j.common.ext.WSSecurityException;
import org.junit.Test;
import org.w3c.dom.Document;

import com.helger.as4lib.attachment.AS4FileAttachment;
import com.helger.as4lib.attachment.IAS4Attachment;
import com.helger.as4lib.encrypt.EncryptionCreator;
import com.helger.as4lib.httpclient.HttpMimeMessageEntity;
import com.helger.as4lib.mime.MimeMessageCreator;
import com.helger.as4lib.signing.SignedMessageCreator;
import com.helger.as4lib.soap.ESOAPVersion;
import com.helger.as4server.client.TestMessages;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.mime.CMimeType;

public class UserMessageManyAttachmentTests extends BaseUserMessageSetUp
{

  public UserMessageManyAttachmentTests (@Nonnull final ESOAPVersion eSOAPVersion)
  {
    super (eSOAPVersion);

  }

  @Test
  public void testUserMessageManyAttachmentsMimeSuccess () throws WSSecurityException, Exception
  {
    final ICommonsList <IAS4Attachment> aAttachments = new CommonsArrayList<> ();
    aAttachments.add (new AS4FileAttachment (ClassPathResource.getAsFile ("attachment/test.xml.gz"),
                                             CMimeType.APPLICATION_GZIP));
    aAttachments.add (new AS4FileAttachment (ClassPathResource.getAsFile ("attachment/test-img.jpg"),
                                             CMimeType.IMAGE_JPG));
    aAttachments.add (new AS4FileAttachment (ClassPathResource.getAsFile ("attachment/test-img2.jpg"),
                                             CMimeType.IMAGE_JPG));

    final MimeMessage aMsg = new MimeMessageCreator (m_eSOAPVersion).generateMimeMessage (TestMessages.testUserMessageSoapNotSigned (m_eSOAPVersion,
                                                                                                                                     null,
                                                                                                                                     aAttachments),
                                                                                          aAttachments,
                                                                                          null);
    MessageHelperMethods.moveMIMEHeadersToHTTPHeader (aMsg, aPost);
    _sendMessage (new HttpMimeMessageEntity (aMsg), true, null);
  }

  @Test
  public void testUserMessageManyAttachmentsSignedMimeSuccess () throws WSSecurityException, Exception
  {
    final ICommonsList <IAS4Attachment> aAttachments = new CommonsArrayList<> ();
    aAttachments.add (new AS4FileAttachment (ClassPathResource.getAsFile ("attachment/test.xml.gz"),
                                             CMimeType.APPLICATION_GZIP));
    aAttachments.add (new AS4FileAttachment (ClassPathResource.getAsFile ("attachment/test-img.jpg"),
                                             CMimeType.IMAGE_JPG));
    aAttachments.add (new AS4FileAttachment (ClassPathResource.getAsFile ("attachment/test-img2.jpg"),
                                             CMimeType.IMAGE_JPG));

    final SignedMessageCreator aSigned = new SignedMessageCreator ();
    final MimeMessage aMsg = new MimeMessageCreator (m_eSOAPVersion).generateMimeMessage (aSigned.createSignedMessage (TestMessages.testUserMessageSoapNotSigned (m_eSOAPVersion,
                                                                                                                                                                  null,
                                                                                                                                                                  aAttachments),
                                                                                                                       m_eSOAPVersion,
                                                                                                                       aAttachments,
                                                                                                                       false),
                                                                                          aAttachments,
                                                                                          null);
    MessageHelperMethods.moveMIMEHeadersToHTTPHeader (aMsg, aPost);
    _sendMessage (new HttpMimeMessageEntity (aMsg), true, null);
  }

  @Test
  public void testUserMessageManyAttachmentsEncryptedMimeSuccess () throws WSSecurityException, Exception
  {
    final ICommonsList <IAS4Attachment> aAttachments = new CommonsArrayList<> ();
    aAttachments.add (new AS4FileAttachment (ClassPathResource.getAsFile ("attachment/test.xml.gz"),
                                             CMimeType.APPLICATION_GZIP));
    aAttachments.add (new AS4FileAttachment (ClassPathResource.getAsFile ("attachment/test-img.jpg"),
                                             CMimeType.IMAGE_JPG));
    aAttachments.add (new AS4FileAttachment (ClassPathResource.getAsFile ("attachment/test-img2.jpg"),
                                             CMimeType.IMAGE_JPG));

    final MimeMessage aMsg = new EncryptionCreator ().encryptMimeMessage (m_eSOAPVersion,
                                                                          TestMessages.testUserMessageSoapNotSigned (m_eSOAPVersion,
                                                                                                                     null,
                                                                                                                     aAttachments),
                                                                          false,
                                                                          aAttachments);
    MessageHelperMethods.moveMIMEHeadersToHTTPHeader (aMsg, aPost);
    _sendMessage (new HttpMimeMessageEntity (aMsg), true, null);
  }

  @Test
  public void testUserMessageManyAttachmentsSignedEncryptedMimeSuccess () throws WSSecurityException, Exception
  {
    final ICommonsList <IAS4Attachment> aAttachments = new CommonsArrayList<> ();
    aAttachments.add (new AS4FileAttachment (ClassPathResource.getAsFile ("attachment/test.xml.gz"),
                                             CMimeType.APPLICATION_GZIP));
    aAttachments.add (new AS4FileAttachment (ClassPathResource.getAsFile ("attachment/test-img.jpg"),
                                             CMimeType.IMAGE_JPG));
    aAttachments.add (new AS4FileAttachment (ClassPathResource.getAsFile ("attachment/test-img2.jpg"),
                                             CMimeType.IMAGE_JPG));

    final SignedMessageCreator aSigned = new SignedMessageCreator ();
    final Document aDoc = aSigned.createSignedMessage (TestMessages.testUserMessageSoapNotSigned (m_eSOAPVersion,
                                                                                                  null,
                                                                                                  aAttachments),
                                                       m_eSOAPVersion,
                                                       aAttachments,
                                                       false);
    final MimeMessage aMsg = new EncryptionCreator ().encryptMimeMessage (m_eSOAPVersion, aDoc, false, aAttachments);
    MessageHelperMethods.moveMIMEHeadersToHTTPHeader (aMsg, aPost);
    _sendMessage (new HttpMimeMessageEntity (aMsg), true, null);
  }

}
