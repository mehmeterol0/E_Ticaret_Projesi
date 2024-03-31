import React, { useState, useEffect } from 'react';
import './ProductController.scss';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import Typography from '@mui/material/Typography';
import { Button, CardActionArea, CardActions, IconButton, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Snackbar } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete'; 
import AddShoppingCartIcon from '@mui/icons-material/AddShoppingCart';

function ProductController() {
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState([]);
  const [total, setTotal] = useState(0);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [snackbarColor, setSnackbarColor] = useState(''); 

  useEffect(() => {
    fetchProducts();
    fetchCart();
  }, []);

  const fetchProducts = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/products/all');
      if (!response.ok) {
        throw new Error('HTTP error, status = ' + response.status);
      }
      const data = await response.json();
      setProducts(data);
    } catch (error) {
      console.error('There was an error!', error);
      setProducts([]);
    }
  };

  const fetchCart = async () => {
    try {
      const response = await fetch('http://localhost:8080/carts/1');
      if (!response.ok) {
        throw new Error('HTTP error, status = ' + response.status);
      }
      const cartData = await response.json();
      if (cartData && cartData.productNames && cartData.productPrices) {
        const productNames = cartData.productNames.split(',');
        const productPrices = cartData.productPrices.split(',');
        const formattedCart = [];
        let totalPrice = 0;
        for (let i = 1; i < productNames.length; i++) {
          const productName = productNames[i].trim();
          const productPrice = parseFloat(productPrices[i].trim()).toFixed(2);
          formattedCart.push({ id: i, name: productName, price: productPrice });
          totalPrice += parseFloat(productPrice);
        }
        setCart(formattedCart);
        setTotal(totalPrice.toFixed(2));
      } else {
        setCart([]);
        setTotal(0);
      }
    } catch (error) {
      console.error('There was an error fetching the cart!', error);
      setCart([]);
      setTotal(0);
    }
  };

  const addToCart = async (product) => {
    try {
      const response = await fetch(`http://localhost:8080/carts/1/products/${product.id}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      });
      if (!response.ok) {
        throw new Error('HTTP error, status = ' + response.status);
      }
      const updatedCartData = await response.json();
      console.log(updatedCartData); 
      if (updatedCartData && updatedCartData.productNames && updatedCartData.productPrices) {
        const productNames = updatedCartData.productNames.split(',');
        const productPrices = updatedCartData.productPrices.split(',');
        const formattedCart = [];
        let totalPrice = 0;
        for (let i = 1; i < productNames.length; i++) {
          const productName = productNames[i].trim();
          const productPrice = parseFloat(productPrices[i].trim()).toFixed(2);
          formattedCart.push({ id: i, name: productName, price: productPrice });
          totalPrice += parseFloat(productPrice);
        }
        setCart(formattedCart);
        setTotal(totalPrice.toFixed(2));
      } else {
        setCart([]);
      }
      setSnackbarColor('success'); 
      setSnackbarMessage('Ürün sepete eklendi!');
      setSnackbarOpen(true);
    } catch (error) {
      console.error('There was an error adding the product to cart!', error);
      setCart([]);
      setSnackbarColor('error'); 
      setSnackbarMessage('Ürün sepete eklenirken bir hata oluştu!');
      setSnackbarOpen(true);
    }
  };

  const removeFromCart = async (productId) => {
    try {
      const response = await fetch(`http://localhost:8080/carts/1/products/${productId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
      });
      if (!response.ok) {
        throw new Error('HTTP error, status = ' + response.status);
      }
      const updatedCartData = await response.json();
      console.log(updatedCartData); 
      if (updatedCartData && updatedCartData.productNames && updatedCartData.productPrices) {
        const productNames = updatedCartData.productNames.split(',');
        const productPrices = updatedCartData.productPrices.split(',');
        const formattedCart = [];
        let totalPrice = 0;
        for (let i = 1; i < productNames.length; i++) {
          const productName = productNames[i].trim();
          const productPrice = parseFloat(productPrices[i].trim()).toFixed(2);
          formattedCart.push({ id: i, name: productName, price: productPrice });
          totalPrice += parseFloat(productPrice);
        }
        setCart(formattedCart);
        setTotal(totalPrice.toFixed(2));
      } else {
        setCart([]);
      }
      setSnackbarColor('success'); 
      setSnackbarMessage('Ürün sepetten kaldırıldı!');
      setSnackbarOpen(true);
    } catch (error) {
      console.error('There was an error removing the product from cart!', error);
      setCart([]);
      setSnackbarColor('error'); 
      setSnackbarMessage('Ürün sepetten kaldırılırken bir hata oluştu!');
      setSnackbarOpen(true);
    }
  };

  const placeOrder = async () => {
    try {
      const response = await fetch('http://localhost:8080/orders/from-cart/1', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      });
      if (!response.ok) {
        throw new Error('HTTP error, status = ' + response.status);
      }
      setSnackbarColor('success'); 
      setSnackbarMessage('Siparişiniz alındı!');
      setSnackbarOpen(true);
      setCart([]);
      setTotal(0);
      window.location.reload(); 
    } catch (error) {
      console.error('There was an error placing the order!', error);
      setSnackbarColor('error'); 
      setSnackbarMessage('Sipariş verilirken bir hata oluştu!');
      setSnackbarOpen(true);
    }
  };

  const clearCart = async () => {
    try {
      const response = await fetch('http://localhost:8080/carts/1/empty', {
        method: 'DELETE',
      });
      if (!response.ok) {
        throw new Error('HTTP error, status = ' + response.status);
      }
      setSnackbarColor('success'); 
      setSnackbarMessage('Sepet başarıyla temizlendi!');
      setSnackbarOpen(true);
      setCart([]);
      setTotal(0);
    } catch (error) {
      console.error('There was an error clearing the cart!', error);
      setSnackbarColor('error'); 
      setSnackbarMessage('Sepeti temizlerken bir hata oluştu!');
      setSnackbarOpen(true);
      setCart([]);
      setTotal(0);
    }
  };

  const handleSnackbarClose = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }
    setSnackbarOpen(false);
  };

  return (
    <div className="ProductPage">
      <div className="ProductList" style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start', marginLeft: '20px', maxWidth: '100%' }}>
        {products && products.length > 0 ? (
          products.map((product) => (
            <div key={product.id} style={{ alignSelf: 'flex-start', marginBottom: '20px', width: '50%' }}>
              <Card sx={{ display: 'flex', width: '100%' }}>
                <CardMedia
                  component="img"
                  height="200px"
                  image={product.imageUrl}
                  alt={product.name}
                  sx={{ objectFit: "contain" }}
                />
                <div style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between', width: '100%' }}>
                  <CardContent>
                    <Typography gutterBottom variant="h5" component="div">
                      {product.name}
                    </Typography>
                    <Typography variant="body2" color="text.primary">
                      Stok Adeti: {product.stock}
                    </Typography>
                  </CardContent>
                  <CardContent style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', paddingRight: '16px' }}>
                    <Button variant="contained" color="primary" onClick={() => addToCart(product)} style={{ marginRight: 'auto' }}>
                      <AddShoppingCartIcon style={{ marginRight: '5px', width: '20px', height: '20px' }} />
                      Sepete Ekle
                    </Button>
                    <Typography variant="h6" color="text.primary" style={{ fontWeight: 'bold', textAlign: 'center' }}>
                      Fiyat: {product.price} ₺
                    </Typography>
                  </CardContent>
                </div>
              </Card>
            </div>
          ))
        ) : (
          <div>Ürün Bulunamadı</div>
        )}
      </div>
      <div className="Cart" style={{ marginLeft: 'auto', marginRight: '20px', width: '25%' }}>
        <Stack direction="row" alignItems="center" spacing={1} style={{ marginBottom: '10px', textAlign: 'center' }}>
          <img src="https://imzalikitabim.com/wp-content/uploads/2018/02/S%C4%B0TE-%C4%B0CON-03-min.png" alt="sepet-icon" style={{ width: '50px', height: '50px' }} />
          <h2 style={{ margin: '0 auto' }}>Sepet</h2>
        </Stack>
        {cart && cart.length > 0 ? (
          <>
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Ürün</TableCell>
                    <TableCell align="right">Fiyat</TableCell>
                    <TableCell></TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {cart.map((item) => (
                    <TableRow key={item.id}>
                      <TableCell>{item.name}</TableCell>
                      <TableCell align="right" style={{ fontWeight: 'bold' }}>{item.price} ₺</TableCell>
                      <TableCell align="right">
                        <IconButton aria-label="delete" onClick={() => removeFromCart(item.id)}>
                          <DeleteIcon />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
            <div className="Total" style={{ display: 'flex', justifyContent: 'space-between', marginTop: '10px' }}>
              <span style={{ fontWeight: 'bold', fontSize: '18px' }}>Toplam:</span>
              <span style={{ fontWeight: 'bold', fontSize: '18px' }}>{total} ₺</span>
            </div>
            <CardActions style={{ justifyContent: 'space-between', marginTop: '10px' }}>
              <Button variant="contained" color="secondary" onClick={clearCart}>
                Sepeti Temizle
              </Button>
              <Button variant="contained" color="primary" onClick={placeOrder}>
                Sipariş Ver
              </Button>
            </CardActions>
          </>
        ) : (
          <div>Sepet Boş</div>
        )}
      </div>
      <Snackbar
        anchorOrigin={{
          vertical: 'top',
          horizontal: 'right',
        }}
        open={snackbarOpen}
        autoHideDuration={3000}
        onClose={handleSnackbarClose}
        message={snackbarMessage}
        // Snackbar rengini dinamik olarak ayarla
        ContentProps={{
          sx: {
            backgroundColor: snackbarColor === 'success' ? 'green' : 'red',
          },
        }}
      />
    </div>
  );
}

export default ProductController;
