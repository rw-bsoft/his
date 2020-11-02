package chis.source.security;

import java.math.BigInteger;
import java.util.Random;

/**
 * Class RSA. Provides math. methods for encryption, decryption, signing and
 * verification. If the keys are known there are constructors to use them, but
 * there is as well a method to generate a new pair of keys.
 * 
 * All parameters are BitIntegers, all return values are BitInteger or arrays of
 * BigInteger as this class provides the funcionality for modular multiplicative
 * inverse calculation.
 * 
 * The modulus must be greater than the number to be encrypted but that should't
 * be a problem with the generated BigIntegers.
 * 
 * @author Bernd Nussbaumer
 * @version $Revision: 0.1 $
 */
public class RSA {

	/**
	 * Variable to store the public exponent.
	 */
	private BigInteger pubExp = new BigInteger("0");

	/**
	 * Variable to store the public modulus.
	 */
	private BigInteger pubMod = new BigInteger("0");

	/**
	 * Variable to store the private key.
	 */
	private BigInteger privKey = new BigInteger("0");

	/**
	 * Key Length in bits.
	 */
	private int keyLength = 0;

	/**
	 * BigIntegers can be generated as primes with a certainty. The higher the
	 * number the slower the process. The probability a generated numer is prime
	 * is: (1 - 1/2^certainty). So a value of 50 means a probability of
	 * 0.9999999999999991.
	 */
	private static final int certainty = 50;

	/**
	 * Empty Constructor.
	 */
	public RSA() {
		this.pubExp = new BigInteger("0");
		this.pubMod = new BigInteger("0");
		this.privKey = new BigInteger("0");
	}

	/**
	 * Constructor with public exponent and public modulus. Everybody can
	 * encrypt and verify a message with this.
	 * 
	 * @param publicE
	 *            the public exponent.
	 * @param publicM
	 *            the public modulus.
	 */
	public RSA(String publicKey) {
		this(publicKey, new BigInteger("0").toString());
	}

	/**
	 * Constructor with public exponent, public modulus and private key. This is
	 * for the recipient who holds the private key.
	 * 
	 * @param publicE
	 *            the public exponent.
	 * @param publicM
	 *            the public modulus.
	 * @param privateK
	 *            the private key.
	 */
	public RSA(String publicKey, String privateKey) {
		int index = publicKey.indexOf("/");
		this.pubExp = new BigInteger(publicKey.substring(0, index));
		this.pubMod = new BigInteger(publicKey.substring(index + 1));
		this.privKey = new BigInteger(privateKey);
	}

	/**
	 * Encryption with public exponent and public modulus. Modulus must be
	 * greater then the message.
	 * 
	 * @param msg
	 *            the message as BigInteger
	 * @return the ciphertext
	 */
	public byte[] encrypt(byte[] msg) throws EncryptException {
		BigInteger biMsg = new BigInteger(msg);
		BigInteger cipher;
		if (this.pubMod.compareTo(biMsg) == 1) {
			cipher = biMsg.modPow(this.pubExp, this.pubMod);
		} else {
			throw new EncryptException(
					"Public modulus must be greater than the message!");
		}
		return cipher.toByteArray();
	}

	/**
	 * Sign a message with private key and public modulus.
	 * 
	 * @param msg
	 *            the message as BigInteger
	 * @return the signature
	 */
	public byte[] sign(String msg) throws EncryptException {
		BigInteger biMsg = new BigInteger(msg.getBytes());
		if (this.privKey.equals(new BigInteger("0"))) {
			throw new EncryptException("can't sign a message without private key.");
		}
		BigInteger signat;
		signat = biMsg.modPow(this.privKey, this.pubMod);
		return signat.toByteArray();
	}

	/**
	 * Verify a message using the public exponent and public modulus.
	 * 
	 * @param signat
	 *            the signature as BigInteger.
	 * @return the message.
	 */
	public byte[] verify(BigInteger signat) {
		BigInteger veri;
		veri = signat.modPow(this.pubExp, this.pubMod);
		return veri.toByteArray();
	}

	/**
	 * Decryption of a message. Only the owner of the private key is able to
	 * encrypt a message using the private key and the public modulus.
	 * 
	 * Return value is m. If this object does not have the private key, then we
	 * throw an exception.
	 * 
	 * @param cipher
	 *            the encrypted message as BigInteger.
	 * @return the decrypted message as BigInteger.
	 */
	public byte[] decrypt(byte[] cipher) throws EncryptException {
		BigInteger biC = new BigInteger(cipher);
		if (this.privKey.equals(new BigInteger("0"))) {
			throw new EncryptException("no decryption without private key.");
		}
		BigInteger msg;
		msg = biC.modPow(this.privKey, this.pubMod);
		return msg.toByteArray();
	}

	/**
	 * Generate a pair of keys. Private key, public exponent and public modulus
	 * are calculated and stored in privKey, pubExp and pubMod.
	 * 
	 * Keys have a length between 512 and 2048 bits in 32bits increments.
	 * 
	 * @param keyL
	 *            the length of the key in bits. If keyL is zero then a random
	 *            length for the key is generated with above restrictions.
	 * 
	 * @return the length of the generated keys.
	 */
	public int generateKeys(int keyL) {
		BigInteger one = new BigInteger("1");
		Random rand = new Random();
		boolean found = false;
		int keyLength = keyL;
		if (keyL <= 0) {
			keyLength = rand.nextInt(49);
			keyLength = 512 + (keyLength * 32);
		}
		this.keyLength = keyLength;

		BigInteger p = new BigInteger("0");
		BigInteger q = new BigInteger("0");
		BigInteger p1 = new BigInteger("0");
		BigInteger q1 = new BigInteger("0");
		BigInteger phi = new BigInteger("0");
		BigInteger e = new BigInteger("0");

		p = new BigInteger(keyLength / 2, certainty, new Random()); // searching
																	// first
																	// prime
		q = new BigInteger(keyLength / 2 + 1, certainty, new Random()); // searching
																		// second
																		// prime

		do {
			e = new BigInteger(keyLength, certainty, new Random()); // searching
																	// prime for
																	// public
																	// exponent
			p1 = p.subtract(one);
			q1 = q.subtract(one);
			phi = p1.multiply(q1); // totient
			if (phi.gcd(e).equals(one)) {
				found = true;
			}
		} while (!found); // public exponent has to be coprime of phi

		BigInteger pubMod = p.multiply(q); // public key modulus
		BigInteger pubExp = e; // public key exponent
		BigInteger privateK = pubExp.modInverse(phi); // the private key

		this.privKey = privateK;
		this.pubExp = pubExp;
		this.pubMod = pubMod;

		return this.keyLength;
	}

	/**
	 * Retrieve key length.
	 * 
	 * @return the key length in bits as an integer.
	 */
	public int getKeyLength() {
		return this.keyLength;
	}

	/**
	 * Retrieve the public key.
	 * 
	 * @return an array - at index 0 is the public exponent, at index 1 the
	 *         public modulus.
	 */
	public String getPublicKey() {
		return pubExp.toString() + "/" + pubMod.toString();
	}

	/**
	 * Retrieve the public key.
	 * 
	 * @return an array - at index 0 is the public exponent, at index 1 the
	 *         public modulus.
	 */
	public String getPrivateKey() {
		return privKey.toString();
	}
}